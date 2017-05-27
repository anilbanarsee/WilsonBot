/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.discordbot;

import abanstudio.discordbot.Main;
import abanstudio.command.Action;
import abanstudio.command.Command;
import abanstudio.command.CoreAction;
import abanstudio.discordbot.wilson.WilsonServer;
import abanstudio.exceptions.InvalidSearchException;
import abanstudio.module.Module;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.Event;
import sx.blah.discord.api.events.EventSubscriber;
import sx.blah.discord.handle.impl.events.DisconnectedEvent;
import sx.blah.discord.handle.impl.events.DiscordDisconnectedEvent;
import sx.blah.discord.handle.impl.events.MessageEmbedEvent;
import sx.blah.discord.handle.impl.events.MessageReceivedEvent;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.handle.impl.obj.Role;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.handle.obj.IMessage;
import sx.blah.discord.handle.obj.IRole;
import sx.blah.discord.handle.obj.IUser;
import sx.blah.discord.handle.obj.IVoiceChannel;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;
import sx.blah.discord.util.MessageBuilder;
import sx.blah.discord.util.MissingPermissionsException;
import sx.blah.discord.util.RateLimitException;
import sx.blah.discord.util.RequestBuffer;
import sx.blah.discord.util.audio.AudioPlayer;

/**
 *
 * @author User
 */
abstract public class BotServer{
    
    
    protected ArrayList<Command> commands;
    public IDiscordClient client;
    protected ArrayList<IRole> roles;
    public Matcher matcher;
    protected String[][] commData;
    protected HashMap<String, Action> actionMap;
    protected HashMap<String, Action> overrideMap;
    protected HashMap<String, CoreAction> overrides;
    protected HashMap<String, CoreAction> eventListeners;
    protected EventListener listener = null;
    protected ArrayList<Module> modules;
    protected Module onMessageOverrider = null;

    
    
    public String prefix;
    
    public BotServer(IDiscordClient client){
        this.client = client;
        modules = new ArrayList<>();
        overrides = new HashMap<>();
        overrideMap = new HashMap<>();
        eventListeners = new HashMap<>();
        initalizeCommands();
        initalizeEventListener();
        
        
    }
    
    @EventSubscriber
    public void onReady(ReadyEvent event){
        
        onServerReady(event);
        System.out.println("Modules setting up...");
        for(Module m: modules){
            System.out.println("["+m+"]");
            m.onReady();
            System.out.println("Module :"+m+", ready");
        }
        System.out.println("Modules ready");
        System.out.println("Setup complete");
        

    }
    protected abstract void onServerReady(ReadyEvent event);
    @EventSubscriber
    public void onMessage(MessageReceivedEvent event){
        
        CoreAction action = overrides.get("onMessage");
        if(action!=null){
            action.exec(event);
        }
        else{
            if(event.getMessage().getAuthor().isBot())
                return;
            String message = event.getMessage().getContent();
        
            if(message.startsWith(prefix+" ")){
            
           
                String command = message;
                parseCommand(command, event.getMessage());
           
            }
        }
        
    }
    @EventSubscriber
    public void onDisconnect(DisconnectedEvent event) throws DiscordException{
        System.out.println("Bot disconnected with reason "+event.getReason()+". Reconnecting...");
    }
    protected abstract void initalizeActions();
    protected abstract void initalizeCommData();
    protected abstract void initalizeEventMethods();
    
    private void initalizeEventListener(){
        if(listener==null){
            listener = new EventListener();
            client.getDispatcher().registerListener(listener);
        }
        else{
            client.getDispatcher().unregisterListener(listener);
        }
        
        listener.getMethodMap();
        Method[] methods = this.getClass().getMethods();
        ArrayList<Method> coreMethods = new ArrayList<>();
        
        for(Method method : methods){
            Annotation[] annotations = method.getAnnotations();
            if(method.getParameterCount()==1){
                for(Annotation annotation: annotations){
                //System.out.println(c);
                    if(annotation.annotationType().equals(EventSubscriber.class)){
                        coreMethods.add(method);
                        break;
                    }
                }
            }
        }
        for(Method m : coreMethods){

            //MethodTuple mt = listener.getMethodMap().get(m.getParameterTypes()[0]);
            ArrayList<MethodTuple> mtlist = new ArrayList<>();
            mtlist.add(new MethodTuple(m, this));
            ArrayList<MethodTuple> mt = listener.getMethodMap().putIfAbsent(m.getParameterTypes()[0], mtlist);
            
            if(mt!=null){
                System.out.println("WARNING : On initialization a botserver : "+this.getClass()+" attempted to overwrite it's own event listener "+m+". This usually occurs because you have two methods with @EventSubscriber and with the same parameter event type. The second method was ignored");
            }
        }
        System.out.println("");
        
        
    }
    private void initalizeCommands(){
             
        initalizeActions();
        initalizeCommData();
        
        for(Action a: actionMap.values()){
            a.setOrigin(this);
        }
        for(Action a: overrideMap.values()){
            a.setOrigin(this);
        }
        
        commands = new ArrayList<>();
        
        for(Entry<String, Action> entry : actionMap.entrySet()){
            
            String[] cData = {};
            boolean flag = false;
            
            for(String[] array : commData){
                if(array[1].equals(entry.getKey())){
                    flag = true;
                    cData = array;
                }
            }
            
            String key = "";
            if(!flag){
                key = entry.getKey();
                cData = new String[3];
                cData[0] = key;
                cData[1] = key;
                cData[2] = "No information sry :(";
                key = entry.getKey();
            }
            else{
                key = cData[1];
            }
            
            
            int n = -345;
            boolean parsed = false;
            try{
                n = Integer.parseInt(cData[cData.length-1]);
                parsed = true;
            }
            catch(NumberFormatException e){
                commands.add(new Command(actionMap.get(key),cData));
                    
            }
            if(parsed)
                commands.add(new Command(actionMap.get(key),cData,n));
                
            
            

        }
        if(!overrideMap.isEmpty())
        for(Entry<String, Action> entry : overrideMap.entrySet()){
            
            String[] cData = {};
            boolean flag = false;
            
            for(String[] array : commData){
                if(array[1].equals(entry.getKey())){
                    flag = true;
                    cData = array;
                }
            }
            
            String key = "";
            if(!flag){
                key = entry.getKey();
                cData = new String[3];
                cData[0] = key;
                cData[1] = key;
                cData[2] = "No information sry :(";
                key = entry.getKey();
            }
            else{
                key = cData[1];
            }
            
            
            int n = -345;
            boolean parsed = false;
            try{
                n = Integer.parseInt(cData[cData.length-1]);
                parsed = true;
            }
            catch(NumberFormatException e){
                commands.add(new Command(overrideMap.get(key),cData));
                    
            }
            if(parsed)
                commands.add(new Command(overrideMap.get(key),cData,n));
                
            
            

        }

        
                    
    }
    @EventSubscriber
    public void onFileRecieved(MessageEmbedEvent event){
        System.out.println("File Recieved Event");
    }
    public boolean parseCommand(String input, IMessage message){
        
        input = input.substring(4);
        String[] split = input.split(" ");
        Command loaded = null;
        ArrayList<String> args = new ArrayList<String>();
        boolean isCommand = false;
        for(String s : split){
            if(s.equals("")){
                continue;
            }
            Command c = Command.matchCommand(commands, s);
            
            if(c!=null){
                if(loaded!=null){
                    if(canUse(loaded.getAdminLevel(),message.getAuthor(),message.getGuild())){
                        String[] array = new String[args.size()];
                        loaded.getAction().exec(args.toArray(array), message);
                        args.clear();
                        isCommand = true;
                    }
                    else{
                        sendMessage(message.getChannel(),"You do not have permission to use this command");
                    }
                }
                loaded = c;
            }
            else{
                args.add(s);
            }
        }
        if(loaded!=null){
            if(canUse(loaded.getAdminLevel(),message.getAuthor(),message.getGuild())){
                String[] array = new String[args.size()];
                loaded.getAction().exec(args.toArray(array), message);
                args.clear();
                isCommand = true;
            }
            else{
                sendMessage(message.getChannel(),"You do not have permission to use this command");
            }
        }
       

        return isCommand;
    }  
    public void addModule(Module module){

        
        Method[] methods = module.getClass().getMethods();
        ArrayList<Method> coreMethods = new ArrayList<>();
        ArrayList<Method> replaceMethods = new ArrayList<>();
        
        for(Method method : methods){
            Annotation[] annotations = method.getAnnotations();
            if(method.getParameterCount()==1){
                boolean eSub = false;
                boolean replace = false;
                for(Annotation annotation: annotations){
                //System.out.println(c);
                    if(annotation.annotationType().equals(EventSubscriber.class)){
                        eSub = true;
                    }
                    if(annotation.annotationType().equals(Replaces.class)){
                        replace = true;
                    }
                }
                if(eSub){
                    if(replace){
                        replaceMethods.add(method);
                    }
                    else{
                        coreMethods.add(method);
                    }
                }
            }
        }
        for(Method m : coreMethods){

            ArrayList<MethodTuple> mt = listener.getMethodMap().get(m.getParameterTypes()[0]);
            
            if(mt==null){
                mt = new ArrayList<>();
                mt.add(new MethodTuple(m,module));
                listener.getMethodMap().put(m.getParameterTypes()[0], mt);
                System.out.println("Module "+module+" added listener event "+m.getName()+" sucessfully. There were no previous methods that were overridden");

            }
            else{
                mt.add(new MethodTuple(m,module));
                System.out.println("Module "+module+" added listener event "+m.getName()+" sucessfully.");
 
            }
            
        }
        for(Method m : replaceMethods){
            ArrayList<MethodTuple> mt = listener.getMethodMap().get(m.getParameterTypes()[0]);
            
            if(mt==null){
                mt = new ArrayList<>();
                mt.add(new MethodTuple(m,module));
                System.out.println("Module "+module+" sucessfully added method "+m+" to event listener. There was no previous implementation of this signature");
            }
            else{
                boolean added = false;
                for(MethodTuple tuple : mt){
                    if(BotServer.class.isAssignableFrom(tuple.getObject().getClass())){
                        tuple.setMethodAndObject(m, module);
                        added = true;
                        System.out.println("Module "+module+" sucessfully overrided BotServer implementation of method "+m+".");
                        break;
                    }
                }
                if(!added){    
                    mt.add(new MethodTuple(m, module));
                    System.out.println("Module "+module+" sucessfully added EventListener method "+m+". There was no previous BotServer implementation of this method");
                }
            }
        }
        
        /*
        if(module.overridesMethods()){
            
            for(HashMap.Entry<String, CoreAction> pair : module.getOverrides().entrySet()){
                String key = pair.getKey();
                if(overrides.get(key)!=null){
                    System.out.println("This module overrided core method "+key+" successfully");
                    overrides.put(key, pair.getValue());
                }
                else{
                    System.out.println("This module attempted to override core method, however it has already been overwritten by another module");
                }
            }
        }*/
                
        ArrayList<Command> moduleCommands = module.getCommands();
        HashMap<String, Action> overrideActions = module.getOverrides();

        int commRejected = 0;
        int commAdded = 0;
        int commOver = 0;
        
        
        modules.add(module);
        

        
        for(Command cX: moduleCommands){
            boolean flag = true;
            for(Command cY: this.commands){ 
                if(cX.getComm().equals(cY.getComm())){
                    Action a = overrideActions.get(cX.getComm());
                    if(a!=null){
                        if(Module.class.isAssignableFrom(cY.getAction().getOrigin().getClass())){
                            System.out.println("Module "+module+" attempted to override base command '"+cY.getComm()+"'. However, this was already overrided by module "+cY.getAction().getOrigin());
                        }
                        else{
                            cY.setAction(a);
                            commOver++;
                            System.out.println("Module "+module+" sucessfully overrided base command '"+cY.getComm()+"'.");
                            break;
                        }
                    }
                    else{
                    
                        System.out.println("Error : Module "+module.getName()+" tried to add command "+cX.getComm()+" which was already present. Skipping this command, module will still be added. Add the command to 'overrides' in the module class if you want to override an existing command in a botserver");
                        flag = false;
                        commRejected++;
                        break;
                    }
                }
            }
            if(flag){
                this.commands.add(cX);
                commAdded++;
            }
        }
        

        

        System.out.println("Loaded module '"+module.getName()+"'. Commands added :"+commAdded+". Commands rejected due to merge conflicts :"+commRejected+". Base commands ovewritten "+commOver+".");
    }
    
    public boolean matches(String s, String regex){
            Pattern p = Pattern.compile(regex);
            matcher = p.matcher(s);
            
            return matcher.find();
    }
    public void sendMessage(IChannel channel, String message){

        RequestBuffer.request(() -> {
		try {
			new MessageBuilder(client).withChannel(channel).withContent(message).build();
		} catch (DiscordException | MissingPermissionsException e) {
			e.printStackTrace();
		}
		return null;
	});

    }
    public void join(String[] arguments, IMessage message){
        IChannel channel = message.getChannel();
        
        if(arguments.length==0){
            sendMessage(channel,"Nigga, tell me where you want me to go, give a channel name or use 'me' if you want me to join you");
        }
        String argument = arguments[0];
        for(int i = 1; i<arguments.length; i++){
            argument += " "+arguments[i];
        }
        
        if(argument.equals("me")){
           
            for(IVoiceChannel vc : message.getAuthor().getConnectedVoiceChannels()){
                if(vc.getGuild().getID().equals(message.getGuild().getID())){
                    try {
                        vc.join();
                    } catch (MissingPermissionsException ex) {
                        sendMessage(message.getChannel(),"I don't have permissions to join that channel");
                    }
                    return;
                }
            }
            sendMessage(message.getChannel(),"You are not in a voicechannel");
            return;
        }
        for(IVoiceChannel vchan : message.getGuild().getVoiceChannels()){

            
            if(vchan.getName().equals(argument)){
                sendMessage(channel,"On my way to "+argument+", dog");
                try {
                    vchan.join();
                } catch (MissingPermissionsException ex) {
                    sendMessage(message.getChannel(),"I don't have permissions to join that channel");
                }
                return;
            }
            
        }
        sendMessage(channel,"Stop playing nigga there ain't no "+argument+" channel in this guild.");
        

        
    }
    public void leave(IMessage message){
        String guildID = message.getGuild().getID();
        
        IVoiceChannel vc;
        try {
            vc = getVoiceChannel(guildID);
        } catch (InvalidSearchException ex) {
            return;
        }
        
        vc.leave();
        
    }
    public IVoiceChannel getVoiceChannel(String guildID) throws InvalidSearchException{
        
        List<IVoiceChannel> channels = client.getConnectedVoiceChannels();
        
        for(IVoiceChannel chan : channels){
            if(chan.getGuild().getID().equals(guildID)){
                return chan;
            }
        }
        
        throw new InvalidSearchException();
        
    }
    public static void sendFile(IChannel channel, File f){
        
        try {
            channel.sendFile(f);
        } catch (IOException | MissingPermissionsException | RateLimitException | DiscordException ex) {
            Logger.getLogger(BotServer.class.getName()).log(Level.SEVERE, null, ex);
        }
       
    }
    public boolean canUse(int adminLevel, IUser user, IGuild guild){
       
        if(adminLevel == 0){
            return true;
        }
        
        else if(adminLevel >= 1){
            return isAdmin(user, guild);
        }
        
        else{
            return false;
        }
        
    }
    public boolean isAdmin(IUser user, IGuild guild){
         
        
        List<IRole> roles = guild.getRolesForUser(user);
        //System.out.println("Admin :"+Permissions.ADMINISTRATOR.ordinal());
        for(IRole role: roles){
            
            System.out.println(role.getName());
            for(Permissions p : role.getPermissions()){
               if(p.hasPermission(Permissions.ADMINISTRATOR.ordinal())){
                   return true;
               }
            }
        }
        return false;
    }
}

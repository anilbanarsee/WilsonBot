/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package abanstudio.wilsonbot;

/**
 *
 * @author Reetoo
 */
public class InvalidDownloadException extends Exception {

    /**
     * Creates a new instance of <code>InvalidDownloadType</code> without detail
     * message.
     */
    public InvalidDownloadException() {
    }

    /**
     * Constructs an instance of <code>InvalidDownloadType</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public InvalidDownloadException(String msg) {
        super(msg);
    }
}

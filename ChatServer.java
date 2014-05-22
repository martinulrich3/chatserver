/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package chatserver;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;

/**
 *
 * @author martin
 */
public class ChatServer {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws ClosedChannelException, IOException  {
        // TODO code application logic here
        Server server=new Server (2000);
        server.Naslouchej();
    
    }
    
}

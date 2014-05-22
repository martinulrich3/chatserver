/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
import java.lang.StringBuilder;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author martin
 */
public class Server {

    private ServerSocketChannel prijimac;
    private CharBuffer pracovniBuffer;
    private ByteBuffer prijimaciBuffer;
    private Selector selektor;
    private final int port;
    private SelectionKey klic;
    private Map<String,SocketChannel> seznam;

    public Server(int port) {
        seznam = new HashMap<>();
        this.port = port;
        try {
            prijimac = ServerSocketChannel.open();
            selektor = Selector.open();

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
        }
        pracovniBuffer = CharBuffer.allocate(65536);
        prijimaciBuffer = ByteBuffer.allocate(65536);

    }

    public void Naslouchej() throws CharacterCodingException, ClosedChannelException, IOException {
        try {
            prijimac.socket().bind(new InetSocketAddress(port));
            prijimac.configureBlocking(false);
            prijimac.register(selektor, SelectionKey.OP_ACCEPT);

        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.getLocalizedMessage());
        }
        SocketChannel socketChannel = null;

        while (true) {
            selektor.select(); // vybere klice
            Set klice = selektor.selectedKeys(); // ziska klice
            Iterator i = klice.iterator();
            while (i.hasNext()) { // projede klice
                klic = (SelectionKey) i.next();
                i.remove();

//pokud je true klient zada o pripojeni
                if (klic.isAcceptable()) {
                    PrijmiKlienta();
                }

                //pokud je true z klienta lze cist
                if (klic.isReadable()) {
                    Cti();
                    PracujsDaty();
                }

            }

        }
    }

    private void PracujsDaty() throws CharacterCodingException, IOException {
        Paket paket = new Paket(ZpracujData());
        switch (paket.VratDruhOperace()) {
            case PRIHLASENI:
                Prihlas(paket);
                ObnovCL();
                break;
            case PRIJMOUT_ZPRAVU:
                OdesliZpravu(paket);
                break;
        }

    }

    private void ObnovCL() throws IOException {
        ArrayList<String> jmenaKlientu = new ArrayList<>(ZiskejJmenaKlientu());
        Paket paket = new Paket(DruhOperace.OBNOV_CL, VytvorSeznamKlientu(jmenaKlientu), "server", "");
        SocketChannel client=null;
        Iterator it=seznam.values().iterator();
        while (it.hasNext())
        {
            client= (SocketChannel) it.next();
            prijimaciBuffer.clear();
            prijimaciBuffer.put(paket.VratData().getBytes());
            prijimaciBuffer.flip();
            while (prijimaciBuffer.hasRemaining()) {
                client.write(prijimaciBuffer);
            }
        }
        
    }

    private ArrayList<String> ZiskejJmenaKlientu() {
        ArrayList<String> jmena = new ArrayList<>(seznam.keySet());
        return jmena;
    }

    private String VytvorSeznamKlientu(ArrayList<String> jmenaKlientu) {
        String text = new String();
        StringBuilder sb = new StringBuilder(text);
        for (int i = 0; i < jmenaKlientu.size() - 1; i++) {
            sb.append(jmenaKlientu.get(i));
            sb.append("&");
        }
        sb.append(jmenaKlientu.get(jmenaKlientu.size() - 1));
        text = sb.toString();
        return text;
    }

    private void OdesliZpravu(Paket paket) throws IOException {
        SocketChannel client = (SocketChannel) klic.channel();
        prijimaciBuffer.clear();
        prijimaciBuffer.put(paket.VratData().getBytes());
        prijimaciBuffer.flip();
        while (prijimaciBuffer.hasRemaining()) {
            client.write(prijimaciBuffer);
        }

    }

    private void Prihlas(Paket paket) {
        seznam.put(paket.VratOdesilatele(),(SocketChannel) klic.channel());
        try {
            ObnovCL();
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String ZpracujData() throws CharacterCodingException {
        prijimaciBuffer.flip();
        Charset charset = Charset.forName("ISO-8859-1");
        CharsetDecoder decoder = charset.newDecoder();
        pracovniBuffer.clear();
        pracovniBuffer = decoder.decode(prijimaciBuffer);
        prijimaciBuffer.clear();
        return pracovniBuffer.toString();
    }

    private void PrijmiKlienta() throws IOException {
        //ziskat socket
        SocketChannel client = prijimac.accept();
        System.out.println("pripojil se jew s adresou:" + client.getRemoteAddress().toString());
        // nastavit na asynchronni
        client.configureBlocking(false);
        // selektor hlida cteni
        client.register(selektor, SelectionKey.OP_READ);
    }

    private void Cti() {

        SocketChannel client = (SocketChannel) klic.channel();
        try {

            client.read(prijimaciBuffer);
        } catch (Exception e) {
            // client is no longer active
            e.printStackTrace();
        }
    }
}

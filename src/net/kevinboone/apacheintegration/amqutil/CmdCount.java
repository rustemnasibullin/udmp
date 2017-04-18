/*==========================================================================
amqutil
CmdManual.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import java.util.*;
import java.io.*;
import java.net.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import javax.jms.*;
import org.apache.commons.io.FileUtils;

/**
 * Implementation of the "count" command 
 */
public class CmdCount extends Cmd
{

  @Override 
  public String getName () { return "count"; }

  @Override 
  public String getShortUsage () { return "amqutil count {options}"; }

  @Override 
  public String getShortDescription () 
    { return "Counts messages on a destination"; }

  @Override 
  public int run() throws Exception
    {
    // Default values of command line arguments
    String host = DEFAULT_HOST; 
    int port = DEFAULT_PORT;
    String user = DEFAULT_USER;
    String pass = DEFAULT_PASS;
    String destination = DEFAULT_DESTINATION; 
    String url = "";  // No default -- if not given, don't use it

    String _destination = cl.getOptionValue ("destination");
    if (_destination != null) destination = _destination;
  
    String _host = cl.getOptionValue ("host");
    if (_host != null) host = _host;
  
    String _port = cl.getOptionValue ("port");
    if (_port != null) port = Integer.parseInt (_port);

    String _user = cl.getOptionValue ("user");
    if (_user != null) user = _user; 

    String _pass = cl.getOptionValue ("password");
    if (_pass != null) pass = _pass; 

    String _url = cl.getOptionValue ("url");
    if (_url != null) url = _url; 

    ActiveMQConnectionFactory factory = getFactory (host, port, url); 

    Connection connection = factory.createConnection(user, pass);
    connection.start();

    Session session = connection.createSession
        (false, Session.AUTO_ACKNOWLEDGE);

    javax.jms.Queue queue = session.createQueue(destination);

    QueueBrowser qb = session.createBrowser (queue);

    Enumeration e = qb.getEnumeration();
    int num = 0;
    while (e.hasMoreElements())
      {
      javax.jms.Message m = (javax.jms.Message)e.nextElement();
      num++;
      }

    qb.close();
    
    connection.close();

    System.out.println ("Messages on " + destination + ": " + num);
    result = destination + ":" + num;
    return 0;
    }

  @Override
  public void setupOptions ()
    {
    super.setupOptions();
    options.addOption ("d", "destination", true, 
      "destination (queue or topic) name");
    options.addOption (null, "host", true, "set server hostname");
    options.addOption ("p", "password", true, "broker password for connection");
    options.addOption (null, "port", true, "set server port");
    options.addOption ("u", "user", true, "broker username for connection");
    options.addOption (null, "url", true, "broker connection url");
    }
  
  
  public static void main (String[] a) {
      
      Cmd cmdq = ListOfCommands.findCmd ("count"); 
      cmdq.setupOptions();
      try {

        cmdq.parseArgs(new String[]{"--destination","deletions"});
        cmdq.doRun();    

      } catch (Throwable ee) {
        ee.printStackTrace();  
      }
      
  }

}






/*==========================================================================
amqutil
Cmd.java
(c)2015 Kevin Boone
Distributed under the terms of the GPL v2.0
==========================================================================*/

package net.kevinboone.apacheintegration.amqutil;
import org.slf4j.*;
import java.io.*;
import org.apache.commons.cli.*;
import org.apache.activemq.*;
import org.apache.commons.io.FileUtils;

/**
 * Base class extended by all amqutil command-line commands. Thus class
 * contains functionality that is common to most (but not all) commands, 
 * such as getting a connection to the broker, processing the --help
 * option.
 */
public abstract class Cmd
{
  protected Options options = null;
  protected CommandLine cl = null; 
  protected Logger logger = null;

  public String result = null;


  /* Default values of various common command-line options */
  public static String DEFAULT_HOST = "localhost";
  public static int DEFAULT_PORT = 61616;
  public static String DEFAULT_USER= "admin";
  public static String DEFAULT_PASS= "admin";
  public static String DEFAULT_DESTINATION = "__test_destination";

  /* Constructor. */
  public Cmd ()
    {
    options = new Options();
    }

  /**
  Set the command-line options that all commands will support. Specidic
  commands will usually have to override this and add others. */
  public void setupOptions()
   {
   options.addOption (null, "time", false, 
      "show time to complete operation in msec");
   options.addOption (null, "help", false, 
      "show brief help");
   options.addOption (null, "loglevel", true, 
      "set log level -- error, info, etc");
   }

  /** Subclasses implement this to do the actual work. Subclasses do not
     need to timing or set the log level, as this class does that in
     th doRun() method */
  public abstract int run() throws Exception; 

  public void setLogger (Logger logger)
    {
    this.logger = logger;
    }

  /**
  doRun wraps the (abstract) run() method in timing and logging options
  */
  public int doRun() throws Exception
    {
    if (cl.hasOption ("help"))
      {
      briefHelp (System.out);
      return 0;
      }

    boolean time = false;
    long start = 0;
    String _logLevel = cl.getOptionValue ("loglevel");
    if (_logLevel != null)
      System.setProperty ("log.level", _logLevel);

    logger = LoggerFactory.getLogger 
      ("net.kevinboone.apacheintegration");
  
    if (cl.hasOption ("time"))
      time = true;
    if (time)
      start = System.currentTimeMillis();

    int ret = run(); // Do the actual work, in the cubclass

    if (time)
      {
      System.out.println ("Time elapsed: " + 
        (System.currentTimeMillis() - start) + " msec");
      }
    return ret;
    }

  /** Parse the command-line arguments. */
  public void parseArgs (String[] args)
      throws ArgParseException
    {
      CommandLineParser clp = new GnuParser();
    try
      {
        
  
      cl = clp.parse (options, args);
      Option v = options.getOption("destination");
      System.out.println ("> "+v.getValue());
      System.out.println ("> "+v.getValueSeparator());

      }
    catch (Exception e)
      {
      e.printStackTrace();  
        
      throw new ArgParseException (e);
      }
    }

  /** Subclasseses override this method to provide a command name. The name
      if matched against the user' s command-line input */
  public abstract String getName();

  /** Subclasseses override this method to provide a description. The
      description is used when the --help option is give */
  public abstract String getShortDescription();

  /** Subclasseses override this method to provide a usage message. The
      description is used when the --help option is give */
  public abstract String getShortUsage();

  /** Print a summary of the command-line options for this command. */
  public void showOptions ()
    {
    HelpFormatter formatter = new HelpFormatter();
    formatter.printHelp ("amqutil " + getName(), options);
    }

  /** Print the usage and description. */
  public void briefHelp (PrintStream p)
    {
    p.println (getShortUsage());
    p.println (getShortDescription());
    showOptions();
    }

  /**
  Gets the ActiveMQ Connection factory from either host/port or URL.
  URL takes precedence.
  */
  ActiveMQConnectionFactory getFactory (String host, int port, 
      String url)
    {
    ActiveMQConnectionFactory factory = null; 
    if (url != null && url.length() != 0)
      {
      factory = new ActiveMQConnectionFactory (url);
      if (!host.equals ("localhost") || port != 61616)
        {
        logger.warn ("Ignoring host/port arguments as a URL was specified");
        }
      }
    else
      {
      factory = new ActiveMQConnectionFactory
          ("tcp://" + host + ":" + port);
      }
    return factory;
    }

  /** 
  Read a file into a string -- used when handling the --file option.
  */
  static String readFile (String path) throws IOException
    {
    return FileUtils.readFileToString (new File (path));
    }


}


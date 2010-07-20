package logging;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.apache.log4j.FileAppender;
public class Logging {
   public Logger logger;
   public Logging(){
	   logger=Logger.getLogger(Logging.class);
	   SimpleLayout layout = new SimpleLayout();	   
	   
	  FileAppender appender = null;
	  try {
	     appender = new FileAppender(layout,"C:\\MailArchive_log.txt",false);
	  } catch(Exception e) {
		  e.printStackTrace();
		  
	  }

  logger.addAppender(appender);
  logger.setLevel((Level) Level.ALL);

   }
   public void debug(String str){
	   logger.debug(str);
   }
   public void info(String str){
	   logger.info(str);
   }
   public void warn(String str){
	   logger.warn(str);
   }
   public void error(String str){
	   logger.error(str);
   }
   public void fatal(String str){
	   logger.fatal(str);
   }
}
        
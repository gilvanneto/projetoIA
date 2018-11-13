package EvolRobot;

/*
 * Bunch of abstract methods that implement behaviour of the bot. (they are called in BotCompiler)
 */
public abstract class Bot {
	
	abstract public String GetVariables();
	
	abstract public String GetRun();
	
	abstract public String GetOnScannedRobot();
	
	abstract public String GetOnHitByBullet();
	
	abstract public String GetOnHitWall();
	
	abstract public String GetOnHitRobot();
	
}

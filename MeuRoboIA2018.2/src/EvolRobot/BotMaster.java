package EvolRobot;

/*
 * Class for evolution of bots.
 */
public abstract class BotMaster {
	protected final int _populationSize;
	
	public BotMaster(int populationSize) {
		_populationSize = populationSize;
	}
	
	abstract public void CreateInitialPopulation();
	
	abstract public void SetFitnesses(double[] fitnesses);
	
	// Make one step of evolution.
	abstract public void Evolve();
	
	abstract public int GetFittestBotId();
	
	abstract public int GetWorstBotId();
	
	abstract public double GetAverageFitness();
	
	abstract public Bot[] GetBots();
	
	abstract public Bot GetBot(int populationId);

	abstract public double GetFitnessOfBot(int populationId);
	
	abstract public int GetPopulationSize();
	
}

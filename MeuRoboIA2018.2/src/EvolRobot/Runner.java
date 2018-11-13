package EvolRobot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import EvolRobot.BattleRunner;
import GABot.GA_Master;

/*
 * Class that takes care of the whole computation. Contains main method!
 */
public class Runner {

	private static String[] _defaultOpponents = { 
		"sample.Crazy", 
		//"sample.Corners", 
//		"sample.SpinBot",
		//"sample.RamFire", 
		//"sample.Target",
		//"sample.MyFirstRobot",
//		"sample.Fire",
		//"MartinTest.MartinTestRobot1*",
		//"sample.TrackFire",
	};
	
	private String _evolutionName;
	private int _generations;
	private int _populationSize;
	
	// fw and bw for file containing on each line all fitnesses of the generation (line ~ generation) 
	FileWriter fsFitnessOut;
	BufferedWriter fitnessOut;
	
	/* fw and bw for file containing on each line (line ~ generation): 
	 * (id of individual with worst fitness in generation) 
	 * (worst fitness in generation) 
	 * (id of individual with best fitness in generation) 
	 * (best fitness in generation)
	 * (average fitness in generation)
	 */
	FileWriter fsFitnessMinIdMaxIdAvg;
	BufferedWriter fitnessMinIdMaxIdAvg;
	
	public boolean _watchBattle = false;
	
	public Runner(String evolutionName, int generations, int populationSize) {
		_evolutionName = evolutionName;
		_generations = generations;
		_populationSize = populationSize;
	}
	
	private void PrepareOutput(String name) throws IOException {
		fsFitnessOut = new FileWriter(BotCompiler._path + "\\" + name + "_GENERATIONS_FITNESSES.txt");
		fitnessOut = new BufferedWriter(fsFitnessOut);
		
		fsFitnessMinIdMaxIdAvg = new FileWriter(BotCompiler._path + "\\" + name + "_GENERATIONS_INFO.txt");
		fitnessMinIdMaxIdAvg = new BufferedWriter(fsFitnessMinIdMaxIdAvg);
	}
	
	private void CloseOutput() throws IOException {
		fitnessOut.close();
		fitnessMinIdMaxIdAvg.close();
	}
	
	// Writes fitnesses of generation info file
	private void WriteFitnesses(double[] fitnesses) throws IOException {
		for (int i = 0; i < fitnesses.length; ++i) {
			if (i > 0)
				fitnessOut.write(", ");
			fitnessOut.write(Double.toString(fitnesses[i])+"\n");
			fitnessOut.flush();
		}
	}
	
	// Writes worst/best/avg fitnesses into file
	private void WriteInfo(int min_id, double min, int max_id, double max, double avg) throws IOException {
		fitnessMinIdMaxIdAvg.write(min_id + " " + Double.toString(min) + " " + max_id + " " + Double.toString(max) + " " + Double.toString(avg)+"\n");
		fitnessMinIdMaxIdAvg.flush();
	}
	
	// Runs battles and returns evaluated fitnesses for generation
	private double[] EvaluateFitness(String[] botNames, String[] opponentNames, boolean watch){
		// generate battle between member and opponents from samples package
		BattleRunner _battleRunner = new BattleRunner();
		double[] fitnesses = _battleRunner.RunBattle(botNames, opponentNames, watch);
		return fitnesses;
	}
	
	// Returns names of bots in generation
	private String[] GetBotNames(int generation) {
		String botPackage = BotCompiler.GetBotPackage();
		String[] botNames = new String[_populationSize];
		for (int populationId = 0; populationId < _populationSize; ++populationId) {
			botNames[populationId] = botPackage + "." + BotCompiler.GetBotName(_evolutionName, generation, populationId) + "*";
		}
		return botNames;
	}
	
	// Returns names of training opponents
	private String[] GetOpponentNames() {
		return _defaultOpponents;
	}
	
	// Deletes the generation of bots (exception for the best one)
	public void ClearBotGeneration(int generation, int populationIdBest, boolean writeStatusText){
		if (writeStatusText)
			System.out.println("Deleting unused bot files");
		
		File oldJavaFile;
		File oldClassFile;
		
		for(int populationId = 0; populationId < _populationSize; populationId++){
			if(populationId == populationIdBest) 
				continue;
			String botFileName = BotCompiler.GetBotFileName(_evolutionName, generation, populationId);
			oldJavaFile = new File(botFileName+".java");
			oldClassFile = new File(botFileName+".class");
			oldJavaFile.delete();
			oldClassFile.delete();
		}
	}
	
	// Compiles the generation of bots
	public void CompileBots(int generation, Bot[] bots){
		for (int populationId = 0; populationId < _populationSize; ++populationId){
			BotCompiler botcompiler = new BotCompiler(_evolutionName, generation, populationId,  bots[populationId]);
			botcompiler.CreateBot();
		}
	}
	
	// 
	public void Run(BotMaster botMaster) throws IOException {
		int populationIdBestLastGen = -1;
		int populationIdBest = -1;
		int populationIdWorst = -1;
		double populationAverageFitness = 0;
		String[] opponentNames = GetOpponentNames();
		long startTime;
		PrepareOutput(_evolutionName);
		
		for (int generation = 0; generation < _generations; ++generation) {
			startTime = System.currentTimeMillis();
			populationIdBestLastGen = populationIdBest;
//			
//			if (generation < 25)
//				opponentNames = new String[]{"sample.Fire",};
//			else
//				opponentNames = new String[]{"sample.TrackFire",};
			
			// create generation
			if (generation == 0)
				botMaster.CreateInitialPopulation();
			else
//				botMaster.GetBots();
				botMaster.Evolve();
			System.out.println("Bots for generation "+generation+ " evolved.");
			
			// compile generation
			CompileBots(generation, botMaster.GetBots());
			System.out.println("Bots for generation "+generation+ " compiled.");
			
			// evaluate fitness for population
			String[] botNames = GetBotNames(generation);
			double[] fitnesses = EvaluateFitness(botNames, opponentNames, _watchBattle);
			botMaster.SetFitnesses(fitnesses);
			System.out.println("Bots for generation "+generation+ " evaluated.");
			
			populationIdBest = botMaster.GetFittestBotId();
			populationIdWorst = botMaster.GetWorstBotId();
			populationAverageFitness = botMaster.GetAverageFitness();
			System.out.println(
					"Best bot in generation " + generation + 
					" is " + populationIdBest + 
					" with FF = " + (botMaster.GetFitnessOfBot(populationIdBest) ));
			System.out.println(
					"Average fitness in generation " + generation + 
					" is " + (populationAverageFitness) + 
					" and cycle took " + (System.currentTimeMillis() - startTime) + " ms ");
			
			
			// clear old generation
			//ClearBotGeneration(generation - 1, populationIdBestLastGen, false);
			
			// write results
			WriteFitnesses(fitnesses);
			WriteInfo(
					populationIdWorst, botMaster.GetFitnessOfBot(populationIdWorst),
					populationIdBest, botMaster.GetFitnessOfBot(populationIdBest),
					populationAverageFitness
					);
		}
		
		CloseOutput();
		System.out.println("Best bot is "+botMaster.GetFittestBotId());
	}
	
	public static void main(String args[]) {
		int populationSize = 10;
		int generations = 30;
		boolean watchBattles = true;
		
		try {
			Runner rga = new Runner("GAFire", generations, populationSize);
			rga._watchBattle = watchBattles;
			BotMaster botMaster = new GA_Master(populationSize);
			rga.Run(botMaster);
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		
	}
}
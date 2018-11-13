package GABot;

import java.util.Collections;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeMap;

import EvolRobot.Bot;
import EvolRobot.BotMaster;

public class GA_Master extends BotMaster {
	
	GA_Bot[] _population;
	double[] _fitnesses;
	Random _rand = new Random();
	
	public static int min = 0;
	public static int max = 200;
	
	public double _mutateProbability = 0.1;
	public double _crossOverProbability = 0.05;
	public double _pseudoTournamentWorstSelectionProb = 0.05;
	public int _pseudoTournamentSize = 5;
	public int _elitism = 10;
	
	public GA_Master(int populationSize) {
		super(populationSize);
	}

	@Override
	public void CreateInitialPopulation() {
		_population = new GA_Bot[_populationSize];
		for (int populationId = 0; populationId < _populationSize; ++populationId)
			_population[populationId] = new GA_Bot();
	}

	@Override
	public void SetFitnesses(double[] fitnesses) {
		_fitnesses = fitnesses;
	}

	@Override
	public void Evolve() {
		GA_Bot[] new_population = new GA_Bot[_populationSize];
		
		int[] elite = GetNFittestIds(_elitism);
		for (int i = 0; i < _elitism; ++i) {
			new_population[i] = new GA_Bot();
			CopyGenes(new_population[i], _population[elite[i]]);
		}
				
		for (int newBotId = _elitism; newBotId < _populationSize; newBotId += 2) {
			GA_Bot newBot1 = new GA_Bot();
			GA_Bot newBot2 = new GA_Bot();
			//CopyGenes(newBot1, _population[PseudoTournamentSelection(_pseudoTournamentSize, _pseudoTournamentWorstSelectionProb)]);
			//CopyGenes(newBot2, _population[PseudoTournamentSelection(_pseudoTournamentSize, _pseudoTournamentWorstSelectionProb)]);
			CopyGenes(newBot1, _population[RouletteSelection()]);
			CopyGenes(newBot2, _population[RouletteSelection()]);
			CrossOver(newBot1, newBot2, _crossOverProbability);			
			MutateBot(newBot1, _mutateProbability);
			MutateBot(newBot2, _mutateProbability);
			new_population[newBotId] = newBot1;
			if (newBotId + 1 < _populationSize)
				new_population[newBotId + 1] = newBot2;
		}
		_population = new_population;
	}
	
	public void CopyGenes(GA_Bot to, GA_Bot from) {
		for (int gene = 0; gene < from.GetSize(); ++gene)
			to.SetGene(gene, from.GetGene(gene));
	}
	
	public void MutateBot(GA_Bot bot, double probability) {
		for (int gene = 0; gene < bot.GetSize(); ++gene)
			if (probability >= _rand.nextDouble())
				bot.SetGene(gene, _rand.nextInt((max - min) + 1) + min);
	}
	
	public void CrossOver(GA_Bot bot1, GA_Bot bot2, double probability) {
		boolean swapping = true;
		for (int gene = 0; gene < bot1.GetSize(); ++gene) {
			if (probability >= _rand.nextDouble()) { 
				if (swapping)
					swapping = false;
				else
					swapping = true;
			}
			
			if (swapping) {
				int temp = bot1.GetGene(gene);
				bot1.SetGene(gene, bot2.GetGene(gene));
				bot2.SetGene(gene, temp);
			}
				
		}
	}
	
	public int PseudoTournamentSelection(int tournamentSize, double probability) {
		// Create a tournament population
        int[] tournament = new int[tournamentSize];
        double[] fitnesses = new double[tournamentSize];
        // For each place in the tournament get a random individual
        for (int i = 0; i < tournamentSize; i++) {
            tournament[i] = i;
            fitnesses[i] = _fitnesses[i];
        }
        // Get the fittest / worst
        int selected = probability > _rand.nextDouble() ? GetIdOfMin(fitnesses) : GetIdOfMax(fitnesses); 
        return selected;
	}
	
	public int RouletteSelection() {
		double fitnessSum = 0.0;
        for (int i = 0; i < _populationSize; i++) {
            fitnessSum += _fitnesses[i];
        }

        double[] rouletteFitnesses = new double[_populationSize];
        for (int i = 0; i < _populationSize; i++) {
        	rouletteFitnesses[i] = _fitnesses[i] / fitnessSum;
        }
        
        double ball = _rand.nextDouble();
        double sum = 0;
        for (int i = 0; i < _populationSize; i++) {
            sum += rouletteFitnesses[i];
            if (sum >= ball) {
                return i;
            }
        }
        return 0;
	}
	

	@Override
	public Bot[] GetBots() {
		return _population;
	}

	@Override
	public Bot GetBot(int populationId) {
		return _population[populationId];
	}
	
	@Override
	public double GetFitnessOfBot(int populationId) {
		return _fitnesses[populationId];
	}

	@Override
	public int GetFittestBotId() {
		return GetIdOfMax(_fitnesses);
	}
	
	@Override
	public int GetWorstBotId() {
		return GetIdOfMin(_fitnesses);
	}
	
	@Override
	public double GetAverageFitness() {
		double sum = 0;
		for (int f = 0; f < _populationSize; ++f)
			sum += _fitnesses[f];
		return sum / _populationSize;
	}
	
	public int[] GetNFittestIds(int n) {
		int[] topN = new int[n];
		TreeMap<Double, Integer> map = new TreeMap<Double, Integer>(Collections.reverseOrder());
		for(int i = 0; i < _populationSize; ++i)
		    map.put(_fitnesses[i], i);
		Iterator mapIterator = map.keySet().iterator();  
		for (int i = 0; i < n; ++i) {
			Double fitness = (Double)mapIterator.next();  
		    int populationId = map.get(fitness);
		    topN[i] = populationId;
		}
		return topN;
	}
	
	public int GetIdOfMax(double[] array) {
		double max = Double.MIN_VALUE;
		int id = -1;
		for (int i = 0; i < array.length; i++) {
            if (max < array[i]) {
            	id = i;
            	max = array[i];
            }
        }
		return id;
	}
	
	public int GetIdOfMin(double[] array) {
		double min = Double.MAX_VALUE;
		int id = -1;
		for (int i = 0; i < array.length; i++) {
            if (min > array[i]) {
            	id = i;
            	min = array[i];
            }
        }
		return id;
	}

	@Override
	public int GetPopulationSize() {
		return _populationSize;
	}



}
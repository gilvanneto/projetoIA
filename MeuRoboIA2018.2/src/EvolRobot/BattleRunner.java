package EvolRobot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;

import robocode.BattleResults;
import robocode.control.BattleSpecification;
import robocode.control.BattlefieldSpecification;
import robocode.control.RobocodeEngine;
import robocode.control.RobotSpecification;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

/*
 * Class that creates battle and gives result of the battle.
 */
public class BattleRunner {

	private static int _battlefieldWidth = 800;
	private static int _battlefieldHeight = 600;
	
	// Class ~ API that allows to run robocode battle and get results from it
	private RobocodeEngine _engine;
	
	// Class holding specifications for battlefield
	private BattlefieldSpecification _battlefieldSpecification;
	
	// Class for obtaining of battle results
	private BattleObserver _battleObserver;

	static final String ROBOCODE_DIR = "C:/Users/Alberto/Desktop/MeuRobo/robocode";
	// Robocode path needed for creation of robocode engine
	private static String _robocodeEngineLocation = "\\Users\\Alberto\\Desktop\\robocode-ia-master\\robocode-ia-master\\robocode";
	
	// Left in here to avoid division of zero errors
	// originally https://en.wikipedia.org/wiki/Handicap_principle
	private static int _handycap = 25;
	
	// Amount of rounds fought in battlefield
	private static int _rounds = 10; 
	
	public BattleRunner() {
		_engine = new RobocodeEngine(new File(_robocodeEngineLocation));
		System.out.println(ROBOCODE_DIR);
		// we add observer to battle, so we can obtain battle results
		_battleObserver = new BattleObserver();
		_engine.addBattleListener(_battleObserver);
		_engine.setVisible(true);
		
		// creation of battlefield specification
		_battlefieldSpecification = new BattlefieldSpecification(_battlefieldWidth, _battlefieldHeight);
	}
	
	// Launches _runs 1v1 battles for each bot vs each opponent and returns their fitnesses
	public double[] RunBattle(String[] botNames, String[] opponentNames, boolean isBattleVisible) {
		
		double[] fitnesses = new double[botNames.length];
		BattleResults[] battleResults;
		String bot;
		String opponent;
		
		for(int i = 0; i < botNames.length; i++){
			// fitness of the bot
			double botFitness = 0;
			for(int j = 0; j < opponentNames.length; j++){
				bot = botNames[i];
				opponent = opponentNames[j];
				
				RobotSpecification[] selectedBots = _engine.getLocalRepository(bot+", "+opponent);
				BattleSpecification battleSpecification = new BattleSpecification(_rounds, _battlefieldSpecification, selectedBots);
				_engine.runBattle(battleSpecification, true);
				
				battleResults = _battleObserver.GetResults();
				int botOrderId = (battleResults[0].getTeamLeaderName().equals(bot) ? 0 : 1);
				int opponentOrderId = (botOrderId == 1 ? 0 : 1);
				
				int botScore = battleResults[botOrderId].getScore();
				int opponentScore = battleResults[opponentOrderId].getScore();
				
				double totalScore = botScore + opponentScore;
				double botFitnessRound = (botScore + _handycap)/(totalScore+_handycap);
				
				botFitness += botFitnessRound;
			}
			// average of each round score
			fitnesses[i] = botFitness / opponentNames.length;	
		}
		
		return fitnesses;
	}
	
}


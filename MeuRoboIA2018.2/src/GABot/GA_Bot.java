package GABot;

import java.util.Random;
import EvolRobot.Bot;

public class GA_Bot extends Bot {
	
	private static int _chromosomeCount = 40;
	private static Random _rand = new Random();
	
	private int[] _genes;
	
	public GA_Bot() {
		_genes = new int[_chromosomeCount];
	    for (int gene=0;gene<_genes.length;gene++)
	    	_genes[gene] = _rand.nextInt(GA_Master.max - GA_Master.min) + GA_Master.min;
	}
	
	public int GetSize() {
		return _chromosomeCount;
	}
	
	public int GetGene(int i) {
		return _genes[i];
	}
	
	public void SetGene(int i, int value) {
		_genes[i] = value;
	}
	
	@Override
	public String GetVariables() {
		return "";
	}

	@Override
	public String GetRun() {
		int startingGeneId = 0;
		return 
			"while(true) {" +
				ObtainCode(8, startingGeneId, false) +
			" } \n"
			;
	}

	@Override
	public String GetOnScannedRobot() {
		int startingGeneId = 8;
		return ObtainCode(8, startingGeneId, true);
	}

	@Override
	public String GetOnHitByBullet() {
		int startingGeneId = 16;
		return ObtainCode(8, startingGeneId, false);
	}

	@Override
	public String GetOnHitWall() {
		int startingGeneId = 24;
		return ObtainCode(8, startingGeneId, false);
	}

	@Override
	public String GetOnHitRobot() {
		int startingGeneId = 32;
		return ObtainCode(8, startingGeneId, false);
	}
	
	private String ObtainCode(int amount, int offset, boolean scan) {
		StringBuilder code = new StringBuilder();
		for (int x = offset; x < offset + amount; x+=2)
			code.append(ObtainCommandFromGenes(_genes[x], _genes[x+1], scan));
		return code.toString();
	}
	
	private String ObtainCommandFromGenes(int command, int value, boolean scan) {
		switch(command % 10){
			case 0:
				return "\n";
			case 1:
				//if (scan)
					return "ahead("+value+");\n";
				//else
				//	return "\n";					
			case 2:
				return "back("+value+");\n";
			case 3:
				return "fire("+value+"%4);\n";
			case 4:
				return "turnGunLeft("+value+");\n";
			case 5:
				return "turnGunRight("+value+");\n";
			case 6:
				return "turnRadarLeft("+value+");\n";
			case 7:
				return "turnRadarRight("+value+");\n";
			case 8:
				return "turnLeft("+value+");\n";
			case 9:
				return "turnRight("+value+");\n";
		}
		return "\n";
	}

}

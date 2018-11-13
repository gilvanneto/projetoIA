package EvolRobot;

import robocode.BattleResults;
import robocode.control.events.BattleAdaptor;
import robocode.control.events.BattleCompletedEvent;

/*
 * This class obtains and returns the results of the battle after its completion.
 */
class BattleObserver extends BattleAdaptor {
	
	private robocode.BattleResults[] _battleResults;
	
	public void onBattleCompleted(BattleCompletedEvent e){
		_battleResults = e.getIndexedResults();
	}
	
	public BattleResults[] GetResults(){		
		return _battleResults;
	}
	
}

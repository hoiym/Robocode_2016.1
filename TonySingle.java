package tonyStank;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import robocode.TeamRobot;
import java.io.IOException;
import java.awt.geom.*;
import java.util.*;
import java.util.ArrayList;

public class TonySingle extends TeamRobot
{
	//Field Specification
	private static Rectangle2D.Double _fieldRect = new java.awt.geom.Rectangle2D.Double(18, 18, 964, 964);

	//Movement
	private byte uni = 1;
	private byte firstScan = 1;
	private int count = 0;
	
	//Guess Factor Targeting
	/*
	ArrayList<BulletWv> listBulWv = new ArrayList<BulletWv>();
	static int[] stats = new int[31];
	int eDir = 1;
	*/
	
	//Enemy identification
	private String name = "";
	private static double enemyEnrg = 200.0;
	private Point2D.Double enemyPos;
	
	//Tony Stank Identification
	private Point2D.Double myPos;
	
	//Wall Smoothing
	//private int stick = 160;
	
	//Wave Surfing
	/*
	private ArrayList listEnemWv;
	private ArrayList surfDirs;
	private ArrayList surfAbsBear;
	*/
	
	//Random Square
	Random rand = new Random();
	private int randomNum = rand.nextInt((40 - 17) + 1) + 17;
	
	public void run()
	{
		//Robot Adjustments
		setColors(Color.black,Color.orange,Color.white); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);
		setAdjustRadarForRobotTurn(true);
		
		//Wave Surfing
		/*
		listEnemWv = new ArrayList();
		surfDirs = new ArrayList();
		surfAbsBear = new ArrayList();
		*/

		
		while(true)
		{
			if (getRadarTurnRemaining() == 0.0)
			{
            	setTurnRadarRightRadians( Double.POSITIVE_INFINITY );
			}
			
			count++;
			
			if(count %  randomNum == 0)
			{
				uni *= -1;
				System.out.println(count);
				randomNum = rand.nextInt((40 - 17) + 1) + 17;
				count = 0;
			}
			
			//setTurnRight(Utils.normalRelativeAngle(wallPrev()));
			
			//Turn Multiplier Lock
			//scan();
			
			setAhead(200 * uni);
			execute();
		}
	}
	
	//Auxiliary Methods
	//--------------------------------------------------------------------------------
	private double normalBearing(double ang)
	{
		while(ang > 180) ang -= 360;
		while(ang < -180) ang += 360;
		return ang; 
	}
	
	public static Point2D.Double project(Point2D.Double sourceLocation,
        double angle, double length)
	{
        return new Point2D.Double(sourceLocation.x + Math.sin(angle) * length,
            sourceLocation.y + Math.cos(angle) * length);
    }
 
    public static double absoluteBearing(Point2D.Double source, Point2D.Double target)
	{
        return Math.atan2(target.x - source.x, target.y - source.y);
    }
 	//--------------------------------------------------------------------------------

	//Wall Smoothing
	/*
	private double wallPrev()
	{
		double x = getX();
		double y = getY();
		double angle = getHeading() + 4 * Math.PI;
		double nx = x + Math.sin(angle) * stick;
		double ny = y + Math.cos(angle) * stick;
		double distx = Math.min(x - 18, getBattleFieldWidth() - x - 18);
		double disty = Math.min(y - 18, getBattleFieldHeight() - y - 18);
		double nDistx = Math.min(distx - 18, getBattleFieldWidth() - nx - 18);
		double nDisty = Math.min(disty - 18, getBattleFieldHeight() - ny - 18);
		double adj = 0;
		java.awt.geom.Rectangle2D.Double fieldRect = new java.awt.geom.Rectangle2D.Double(18, 18, getBattleFieldWidth() - 36, getBattleFieldHeight() - 36);
		
		for(int i = 0; !fieldRect.contains(nx, ny) && i < 25; i++)
		{
			if(nDisty < 0 && nDisty < nDistx)
			{
				angle = ((int)((angle + (Math.PI/2)) / Math.PI)) * Math.PI;
				adj = Math.abs(disty);
			}
			else if(nDistx < 0 && nDistx <= nDisty)
			{
				angle = ((int)((angle + (Math.PI/2)) / Math.PI)) * Math.PI + Math.PI/2;
				adj = Math.abs(distx);
			}
			
			angle += uni * (Math.abs(Math.acos(adj/stick)) + 0.005);
			nx = x + Math.sin(angle) * stick;
			ny = y + Math.sin(angle) * stick;
			nDistx = Math.min(distx - 18, getBattleFieldWidth() - nx - 18);
			nDisty = Math.min(disty - 18, getBattleFieldHeight() - ny - 18);
		}
		
		return angle;
	}
	*/

	public void onScannedRobot(ScannedRobotEvent e)
	{
		// Don't fire on teammates
		if (isTeammate(e.getName())) { return; }	
	
		//First Scan (Looking for enemy's leader)
		if(name == ""){
			name = e.getName();
		}
		else{
			if(name != e.getName()){
				return;
			}
		}
		
		
		//Enemy's
		double enemyDist = e.getDistance();
		
		//Tony Stank's
		myPos = new Point2D.Double(getX(), getY());
		double absEnemBear = e.getBearingRadians() + getHeadingRadians();
		double vel_lat = getVelocity() * Math.sin(e.getBearingRadians());

		//Turn multiplier Lock
		//---------------------------------------------------
		//double radarT = absEnemBear - getRadarHeadingRadians();
		//---------------------------------------------------
		
		//Width Lock
	    double extraT = Math.min( Math.atan(45/enemyDist), Rules.RADAR_TURN_RATE_RADIANS );
		double radarT = Utils.normalRelativeAngle(absEnemBear - getRadarHeadingRadians());
	    radarT += (radarT< 0 ? -extraT: extraT);
		
		//Radar Turning
		setTurnRadarRightRadians(radarT);
		
		//Squaring
	 	setTurnRight(normalBearing(e.getBearing() + 90 - (5 * uni)));
		
		//Linear Targetting
		double bPower = Math.min(3.0, getEnergy());
		double x = getX();
		double y = getY();
		double angle = getHeadingRadians() + e.getBearingRadians();
		double eX = getX() + e.getDistance() * Math.sin(angle);
		double eY = getY() + e.getDistance() * Math.cos(angle);
		
		double eH = e.getHeadingRadians();
		double eV = e.getVelocity();
		double predX = eX; 
		double predY = eY;
		double pred_curDist = Point2D.Double.distance(x, y, predX, predY);
		
		for(int deltaT = 1; (deltaT++) * (20.0 - 3.0 * bPower) < pred_curDist; deltaT++)
		{
			predX += Math.sin(eH) * eV;	
			predY += Math.cos(eH) * eV;
			
			if(predX < 18 || predY < 18 || predX > getBattleFieldWidth() - 18 || predY > getBattleFieldHeight() - 18)
			{
				predX = Math.min(Math.max(18, predX), getBattleFieldWidth()  - 18);	
				predY = Math.min(Math.max(18, predY), getBattleFieldHeight() - 18);
				break;
			}
		}
		
		double theta = Utils.normalAbsoluteAngle(Math.atan2(predX - getX(), predY - getY()));
		setTurnGunRightRadians(Utils.normalRelativeAngle(theta - getGunHeadingRadians()));
		
		
		
		//Guess Fator Targeting
		/*
		for(int i = 0; i < listBulWv.size(); i++)
		{
			BulletWv curBulWv = (BulletWv)listBulWv.get(i);
			
			if(curBulWv.chkHit(eX, eY, getTime()))
			{
				listBulWv.remove(curBulWv);
				i--;
			}
		}
		
		if(e.getVelocity() != 0)
		{
			if (Math.sin(e.getHeadingRadians()-absEnemBear)*e.getVelocity() < 0)
				eDir = -1;
			else
				eDir = 1;
		}
		
		int[] currentStats = stats;
		
		double pw = Math.min(3, Math.max(.1, 400/enemyDist));
		
		BulletWv newWv = new BulletWv(x, y, absEnemBear, pw, eDir, getTime(), currentStats);
		
		int bestindex = 15;
		for (int i=0; i<31; i++)
		{
			if (currentStats[bestindex] < currentStats[i])
			{
				bestindex = i;
			}
		}
		
		double guessfactor = (double)(bestindex - (stats.length - 1) / 2)
                        / ((stats.length - 1) / 2);
		double angleOffset = eDir * guessfactor * newWv.maxEscpAng();
        double gunAdjust = Utils.normalRelativeAngle(absEnemBear - getGunHeadingRadians() + angleOffset);
        setTurnGunRightRadians(gunAdjust);
		*/

		//---------------------------------------------------------
		//Enemy Identification
		
		if(name == "")
		{
			name = e.getName();
		}
		else
		{
			if((name == e.getName()))
			{
				if(enemyDist >= 400 && (count % 10 == 0))
				{
					if(setFireBullet(400/enemyDist) != null)
					{
						//listBulWv.add(newWv);
					}
				}
				/*
				else if((enemyDist < 400 && enemyDist >= 200) && (count %  == 0))
				{
					if(setFireBullet(400/enemyDist) != null)
					{
						//listBulWv.add(newWv);
					}
				}
				*/
				else// if(enemyDist < 200)
				{
					if(setFireBullet(400/enemyDist) != null)
					{
						//listBulWv.add(newWv);
					}
				}
			}
		}
		
		enemyEnrg = e.getEnergy();
		
		Enemy foe = new Enemy(e.getName(), eX, eY, enemyEnrg, enemyDist, eH, eV);

		try {
			broadcastMessage(foe);
		} catch (IOException ex) {
			out.println("Unable to send order: ");
			ex.printStackTrace(out);
		}
	}

	

	public void onHitByBullet(HitByBulletEvent e)
	{
		uni *= -1;
		setBack(100 * uni);
	}
	


	public void onRobotDeath(RobotDeathEvent e)
	{
		if(name == e.getName())
			name = "";
	}
	
	

	public void onHitWall(HitWallEvent e)
	{
		if(getVelocity() == 0){
			uni *= -1;
			count = 1;
			setAhead(100 * uni);
		}
	}
	

	public void onHitRobot(HitRobotEvent e)
	{
		if(isTeammate(e.getName()))
		{
			System.out.println("BATEU---------------------");
			
			if(getVelocity() == 0){
				uni *= -1;
				count = 1;
				setAhead(100 * uni);
			}
			
			return;
		}
		else
		{
			setTurnGunRight(normalBearing(getHeading() - getGunHeading() + e.getBearing()));
			setFire(3);
			if(getVelocity() == 0){
				uni *= -1;
				count = 1;
				setAhead(100 * uni);
			}
			if(getVelocity() == 0){
				uni *= -1;
				count = 1;
				setAhead(100 * uni);
			}
		}
	}
}

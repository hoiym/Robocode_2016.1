package tonyStank;

import java.awt.*;
import robocode.*;
import robocode.Droid;
import robocode.MessageEvent;
import robocode.TeamRobot;
import java.util.*;
import java.awt.geom.*;
import robocode.util.Utils;
import static robocode.util.Utils.normalRelativeAngleDegrees;


public class An extends TeamRobot implements Droid
{
	//Droid's feature
	private byte uni = 1;
	private int count = 0;
	private int aloneCount = 0;
	
	//Enemy Stats
	private double dx;
	private double dy;
	private double theta;
	private double dist;
	
	
	//Random Square
	Random rand = new Random();
	private int randomNum = rand.nextInt((40 - 17) + 1) + 17;
	
	//Linear Targeting
	double x, y, predX, predY, eX, eY;
	double pred_curDist, eH, eV;
	
	public void run() {
		
		setColors(Color.black,Color.orange,Color.white); // body,gun,radar
		setAdjustGunForRobotTurn(true);
		setAdjustRadarForGunTurn(true);

		// Robot main loop
		while(true)
		{
			count++;
			aloneCount++;
			
			if(count % randomNum == 0)
			{
				uni *= -1;
				randomNum = rand.nextInt((40 - 17) + 1) + 17;
				count = 0;
			}

			setAhead(200 * uni);
			
			if(aloneCount <= 15)
				setFire(3);

			execute();
		}
	}

	public void onMessageReceived(MessageEvent e)
	{
		aloneCount = 0;
		
		double x = getX();
		double y = getY();
		
		if (e.getMessage() instanceof Enemy)
		{
			Enemy foe = (Enemy) e.getMessage();
			
			dx = foe.retEnemyX() - x;
			dy = foe.retEnemyY() - y;
			
			//Linear Targeting
			theta = Math.toDegrees(Math.atan2(dx, dy));
			dist = Math.sqrt(dx * dx + dy * dy);
			double angle = Math.toRadians(theta);
			double bPower = Math.min(3.0, foe.retEnemyEnrg());
			
			eX = x + dist * Math.sin(angle);
			eY = y + dist * Math.cos(angle);
			eH = foe.retEnemyHead();
			eV = foe.retEnemySpd();
			predX = eX; 
			predY = eY;
			pred_curDist = Point2D.Double.distance(x, y, predX, predY);
			
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
			
			double phi = Utils.normalAbsoluteAngle(Math.atan2(predX - x, predY - y));
			setTurnGunRightRadians(Utils.normalRelativeAngle(phi - getGunHeadingRadians()));
			
			System.out.println(theta + " " + dist);
		}
		
		//setTurnGunRight(normalRelativeAngleDegrees(theta - getGunHeading()));
		//setFire(400/dist);
		
		if(dist <= 300 && dist > 200)
			setFire(2);
		else if(dist> 300)
			setFire(1);
		else
			setFire(3);
		
		//Squaring
		setTurnRight(normalRelativeAngleDegrees(theta - getHeading() + 90 - (15 * uni)));
	}
	
	//Auxiliary Methods
	private double normalBearing(double ang)
	{
		while(ang > 180) ang -= 360;
		while(ang < -180) ang += 360;
		return ang; 
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
		}
	}
	
	public void onHitWall(HitWallEvent e)
	{
		if(getVelocity() == 0){
			uni *= -1;
			count = 1;
			setAhead(100 * uni);
		}
	}
	
	public void onHitByBullet(HitByBulletEvent e)
	{
		uni *= -1;
		count = 1;
		setAhead(100 * uni);
	}
}

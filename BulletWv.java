package tonyStank;

import robocode.*;
import robocode.util.Utils;
import java.awt.*;
import java.awt.geom.*;

public class BulletWv
{
	private double bul_x, bul_y, bul_bear, bul_pw;
	private long fireT;
	private int dir;
	private int[] retSegm;
	
	public BulletWv(double x, double y, double bear, double pw, int d, long t, int []segm)
	{
		bul_x = x;
		bul_y = y;
		bul_bear = bear;
		bul_pw = pw;
		fireT = t;
		retSegm = segm;
	}
	
	public double getBulSpd()	{ return 20 - bul_pw * 3; }
	
	public double maxEscpAng()	{ return Math.asin(8/getBulSpd()); }
	
	public boolean chkHit(double eX, double eY, long curT)
	{
		if(Point2D.distance(bul_x, bul_y, eX, eY) <= (curT - fireT) * getBulSpd())
		{
			double newDir = Math.atan2(eX - bul_x, eY - bul_y);
			double angleOffset = Utils.normalRelativeAngle(newDir - bul_bear);
			double guessFactor = Math.max(-1, Math.min(1, angleOffset / maxEscpAng())) * dir;
			int index = (int) Math.round((retSegm.length - 1) /2 * (guessFactor + 1));
			retSegm[index]++;
			
			return true;
		}
		
		return false;
	}
	
}

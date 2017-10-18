package tonyStank;

public class Enemy implements java.io.Serializable
{
	private static final long serialVersionUID = 1L;
	private String name;
	private double x;
	private double y;
	private double energy;
	private double distance;
	private double heading;
	private double speed;
	
	public Enemy(String scanName, double scanX, double scanY, double scanEnrg, double scanDist, double scanHead, double scanSpeed)
	{
		name = scanName;
		x = scanX;
		y = scanY;
		energy = scanEnrg;
		distance = scanDist;
		heading = scanHead;
		speed = scanSpeed;
	}
	
	public String retEnemyName()
	{ return name; }
	
	public double retEnemyX()
	{ return x;	}
	
	public double retEnemyY()
	{ return y;	}
	
	public double retEnemyEnrg()
	{ return energy;}
	
	public double retEnemyDist()
	{ return distance; }
	
	public double retEnemyHead()
	{ return heading; }
	
	public double retEnemySpd()
	{ return speed; }
	
	public void setNewStats(double newX, double newY, double newEnrg, double newDist, double newHead, double newSpd)
	{
		x = newX;
		y = newY;
		energy = newEnrg;
		distance = newDist;
		heading = newHead;
		speed = newSpd;
	}
	
	/*
	public void setEnemyX(double newX)
	{ x = newX; }
	
	public void setEnemyY(double newY)
	{ y = newY; }
	
	public void setEnemyEnrg(double newEnrg)
	{ energy = newEnrg; }
	
	public void setEnemyDist(double newDist)
	{ distance = newDist; }
	*/
}	
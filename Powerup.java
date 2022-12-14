import java.util.ArrayList;
import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
/**
 * Powerup Class: Manage the activities of the powerup 
 *
 */
public class Powerup {
	public static final int BRICK_WIDTH = 30;
	public static final int BRICK_HEIGHT = 20;
	public static final String GREEN_POWER = "greenpower.gif";
	private ArrayList<Powerup> myPowerups;
	private ImageView powerup;
	private Brick brick;
	private String powerType;
	private Timer timer;
	private boolean ableToHit;
	/**
	 * Powerup constructor
	 * 
	 * @param brick
	 */
	public Powerup(Brick b) {
		Image pu = new Image(getClass().getClassLoader().getResourceAsStream(GREEN_POWER));
		powerup = new ImageView(pu);
		brick = b;
		powerType = "";
		ableToHit = true;
	}
	/**
	 * Return the ImageView of powerup
	 * 
	 * @return
	 */
	public ImageView getPowerupIV() {
		return this.powerup;
	}
	/**
	 * Randomly select a power
	 * 
	 * @return
	 */
	public String setPower() {
		ArrayList<String> powerList = new ArrayList<String>();
		powerList.add("bigPaddle");
		powerList.add("smallPaddle");
		powerList.add("slowPuck");
		powerList.add("fastPuck");
		powerList.add("missile");
		Collections.shuffle(powerList);
		return powerList.get(0);
	}
	/**
	 * Return an ArrayList of powerups
	 * 
	 * @param myBricks
	 * @return
	 */
	public ArrayList<Powerup> createPowerups(ArrayList<Brick> myBricks) {
		Collections.shuffle(myBricks);
		myPowerups = new ArrayList<Powerup>();
		for (int i = 0; i < 12; i++) {
			Powerup pu = new Powerup(myBricks.get(i));
			pu.powerup.setFitWidth(20);
			pu.powerup.setFitHeight(20);
			pu.powerup.setX(myBricks.get(i).getBrickIV().getX() + BRICK_WIDTH / 4);
			pu.powerup.setY(myBricks.get(i).getBrickIV().getY());
			pu.powerup.setVisible(false);
			pu.powerType = setPower();
			myBricks.get(i).addPower();
			myPowerups.add(pu);
		}
		return myPowerups;
	}
	/**
	 * Check if the paddle has catched the falling powerup
	 * 
	 * @param myPaddle
	 * @return
	 */
	public boolean checkHitPaddle(ImageView myPaddle) {
		return this.ableToHit && this.powerup.getBoundsInParent().intersects(myPaddle.getBoundsInParent());
	}
	/**
	 * Activate corresponding power
	 * 
	 * @param powerType
	 * @param myPaddle
	 * @param myBouncer
	 * @param myMissiles
	 */
	public void activatePower(String powerType, ImageView myPaddle, Bouncer myBouncer, ArrayList<Missile> myMissiles) {
		if (powerType == "bigPaddle") {
			bigPaddle(myPaddle);
		} else if (powerType == "smallPaddle") {
			smallPaddle(myPaddle);
		} else if (powerType == "slowPuck") {
			slowPuck(myBouncer);
		} else if (powerType == "fastPuck") {
			fastPuck(myBouncer);
		} else if (powerType == "missile") {
			resetMissiles(myMissiles);
			activateMissile(myPaddle, myMissiles);
		}
	}
	/**
	 * Reset used missiles
	 * 
	 * @param myMissiles
	 */
	public void resetMissiles(ArrayList<Missile> myMissiles) {
		for (Missile m : myMissiles) {
			if (m.checkMissile()) {
				m.resetMissile();
			}
		}
	}
	/**
	 * Activate the missile
	 * 
	 * @param myPaddle
	 * @param myMissiles
	 */
	public void activateMissile(ImageView myPaddle, ArrayList<Missile> myMissiles) {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			int count = 0;
			public void run() {
				count++;
				if (count > 6) {
					timer.cancel();
					timer.purge();
					return;
				}
				for (Missile m : myMissiles) {
					if (!m.checkMissile()) {
						m.useMissile();
						m.setInitialMissilePos(myPaddle);
						break;
					}
				}
			}
		}, 0, 2000);
	}
	/**
	 * Increase the paddle size
	 * 
	 * @param myPaddle
	 */
	public void bigPaddle(ImageView myPaddle) {
		myPaddle.setFitWidth(100);
	}
	/**
	 * Decrease the paddle size
	 * 
	 * @param myPaddle
	 */
	public void smallPaddle(ImageView myPaddle) {
		myPaddle.setFitWidth(40);
	}
	/**
	 * Decrease the puck speed
	 * 
	 * @param myBouncer
	 */
	public void slowPuck(Bouncer myBouncer) {
		myBouncer.changeSpeed(120);
	}
	/**
	 * Increase the puck speed
	 * 
	 * @param myBouncer
	 */
	public void fastPuck(Bouncer myBouncer) {
		myBouncer.changeSpeed(360);
	}
	/**
	 * Check if the brick that contains a powerup has been cleared, and if the
	 * user has caught the powerup, if so, activate the powers
	 * 
	 * @param elapsedTime
	 * @param myPowerups
	 * @param myBricks
	 * @param myPaddle
	 * @param myBouncer
	 * @param myMissiles
	 * @return
	 */
	public ArrayList<Powerup> myPowerPos(double elapsedTime, ArrayList<Powerup> myPowerups, ArrayList<Brick> myBricks,
			ImageView myPaddle, Bouncer myBouncer, ArrayList<Missile> myMissiles) {
		for (Powerup pu : myPowerups) {
			if (!myBricks.contains(pu.brick)) {
				pu.powerup.setVisible(true);
				pu.powerup.setY(pu.powerup.getY() + 80 * elapsedTime);
				if (pu.checkHitPaddle(myPaddle)) {
					pu.ableToHit = false;
					pu.activatePower(pu.powerType, myPaddle, myBouncer, myMissiles);
				}
			}
		}
		return myPowerups;
	}
}
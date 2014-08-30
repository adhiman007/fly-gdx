package de.fau.cs.mad.fly.features.overlay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;

import de.fau.cs.mad.fly.Fly;
import de.fau.cs.mad.fly.I18n;
import de.fau.cs.mad.fly.Loader;
import de.fau.cs.mad.fly.features.IFeatureFinish;
import de.fau.cs.mad.fly.features.IFeatureInit;
import de.fau.cs.mad.fly.game.GameController;
import de.fau.cs.mad.fly.profile.LevelProfile;
import de.fau.cs.mad.fly.profile.PlayerProfile;
import de.fau.cs.mad.fly.profile.PlayerProfileManager;
import de.fau.cs.mad.fly.profile.Score;
import de.fau.cs.mad.fly.profile.ScoreDetail;
import de.fau.cs.mad.fly.profile.ScoreManager;
import de.fau.cs.mad.fly.ui.UI;

/**
 * Optional Feature to display a start and a finish message to the player.
 * 
 * @author Tobias Zangl
 */
public class GameFinishedOverlay implements IFeatureInit, IFeatureFinish {
    private GameController gameController;
    private final Skin skin;
    private final Stage stage;
    private Score newScore;
    
    public GameFinishedOverlay(final Skin skin, final Stage stage) {
        this.stage = stage;
        this.skin = skin;
    }
    
    @Override
    public void init(final GameController gameController) {
        this.gameController = gameController;
    }
    
	/**
	 * When the game is finished, 5 states are possible:
	 * <p>
	 * 1) finished in time, not dead:
	 * <p>
	 * 1.1 it is not the last level of the level group, then message, score and next level and back button are shown
	 * <p>
	 * 1.2 it is the last level of the level group, then message, score and next group and back button are shown
	 * <p>
	 * 1.3 it is the last level of the last level group, then message, score and back button are shown 2) spaceship broken:
	 * <p>
	 * 2.1 it is a normal level, then only message, restart and back button are shown
	 * <p>
	 * 2.2 it is a endless level, then show score, restart and back button
	 * <p>
	 */
	@Override
	public void finish() {
		Table outerTable = new Table();
		outerTable.setFillParent(true);

		TextButtonStyle textButtonStyle = skin.get(UI.Buttons.DEFAULT_STYLE, TextButtonStyle.class);
		TextButton backToMainMenuButton = new TextButton(I18n.t("back.to.menu"), textButtonStyle);
		backToMainMenuButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				((Fly) Gdx.app.getApplicationListener()).setMainMenuScreen();
			}
		});

		PlayerProfile currentPlayer = PlayerProfileManager.getInstance().getCurrentPlayerProfile();
		final Table messageTable = new Table();
		NinePatchDrawable background = new NinePatchDrawable(skin.get("grey-progress-bar", NinePatch.class));
		messageTable.setBackground(background);

		if (gameController.getLevel().getGateCircuit().isReachedLastGate()) {

			if (currentPlayer.IsLastLevel()) {
				if (currentPlayer.IsLastLevelGroup()) {
//					if(currentPlayer.getPassedLevelID()!= currentPlayer.getCurrentLevelProfile().id) {
//						currentPlayer.setPassedLevelID(currentPlayer.getCurrentLevelProfile().id);
//						currentPlayer.savePassedLevelID();
//					}
					showInfoLabel(messageTable, "ALLGroupPassed");

					showScore(messageTable);
					// messageTable.add(new
					// Label(I18n.t("ALLGroupPassed"),skin)).pad(UI.Buttons.SPACE_WIDTH);
					messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH).colspan(2);
				} else {
					if( currentPlayer.getPassedLevelgroupID()==currentPlayer.getCurrentLevelGroup().id){
						currentPlayer.setPassedLevelgroupID(currentPlayer.getnextLevelGroup().id);
						currentPlayer.savePassedLevelgroupID();
						currentPlayer.setPassedLevelID(currentPlayer.getnextLevelGroup().getFirstLevel().id);
						currentPlayer.savePassedLevelID();
					}					
					
					showInfoLabel(messageTable, "OneGroupPassed");
					showScore(messageTable);
					
					TextButton nextGroupButton = new TextButton(I18n.t("nextLevelGroup"), textButtonStyle);
					nextGroupButton.addListener(new ClickListener() {
						@Override
						public void clicked(InputEvent event, float x, float y) {
							PlayerProfileManager.getInstance().getCurrentPlayerProfile().setToNextLevelGroup();
							PlayerProfileManager.getInstance().getCurrentPlayerProfile().saveCurrentLevelGroup();
							PlayerProfileManager.getInstance().getCurrentPlayerProfile().saveCurrentLevelProfile();
							// set and load new level
							LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
							Loader.getInstance().loadLevel(levelHead);
						}
					});

					messageTable.add(nextGroupButton).pad(UI.Buttons.SPACE_WIDTH);
					messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH);
				}

			} else {
				if( currentPlayer.getPassedLevelgroupID()==currentPlayer.getCurrentLevelGroup().id
						&& currentPlayer.getPassedLevelID()==currentPlayer.getCurrentLevelProfile().id){
					currentPlayer.setPassedLevelID(currentPlayer.getNextLevel().id);
					currentPlayer.savePassedLevelID();
				}
					
				showInfoLabel(messageTable, "level.congratulations");
				showScore(messageTable);
				TextButton nextLevelButton = new TextButton(I18n.t("nextLevel"), textButtonStyle);
				nextLevelButton.addListener(new ClickListener() {
					@Override
					public void clicked(InputEvent event, float x, float y) {
						PlayerProfileManager.getInstance().getCurrentPlayerProfile().setToNextLevel();
						PlayerProfileManager.getInstance().getCurrentPlayerProfile().saveCurrentLevelProfile();
						// set and load new level
						LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
						Loader.getInstance().loadLevel(levelHead);
					}
				});
				messageTable.add(nextLevelButton).pad(UI.Buttons.SPACE_WIDTH);
				messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH);
			}

		} else if (gameController.getPlayer().isDead()) {
			showInfoLabel(messageTable, "ship.destroyed");

			if (gameController.getLevel().head.name.equals("Endless")) {
				showScore(messageTable);
			}

			TextButton restartButton = new TextButton(I18n.t("restart"), textButtonStyle);
			restartButton.addListener(new ClickListener() {
				@Override
				public void clicked(InputEvent event, float x, float y) {
					// reload the level
					LevelProfile levelHead = PlayerProfileManager.getInstance().getCurrentPlayerProfile().getCurrentLevelProfile();
					Loader.getInstance().loadLevel(levelHead);
				}
			});

			messageTable.add(restartButton).pad(UI.Buttons.SPACE_WIDTH);
			messageTable.add(backToMainMenuButton).pad(UI.Buttons.SPACE_WIDTH);
			messageTable.row().expand();
		}

		outerTable.add(messageTable).center();
		stage.addActor(outerTable);
	}
    
    private void showInfoLabel(final Table messageTable, String info) {
    	Label infoLabel = new Label(I18n.t(info), skin);            		
        messageTable.add(infoLabel).colspan(2);
        messageTable.row().expand();
    }

	/**
	 * @param messageTable
	 */
	private void showScore(final Table messageTable) {
		//Score newScore = gameController.getLevel().getScore();
		newScore = gameController.getScoreController().getEndScore(gameController);
		
		// adds an amount of money to the players profile that equals the score he got in this level
		PlayerProfileManager.getInstance().getCurrentPlayerProfile().addMoney(newScore.getTotalScore());
		
		String scoreString = I18n.t("newScore") + newScore.getTotalScore();
		final Label scoreLabel = new Label(scoreString, skin);
		messageTable.add(scoreLabel).pad(15f);
		messageTable.row().expand();
		for (ScoreDetail detail : newScore.getScoreDetails()) {
		    messageTable.row().expand();
		    messageTable.add(new Label(I18n.t(detail.getDetailName()), skin)).pad(6f).uniform();
		    messageTable.add(new Label(detail.getValue(), skin)).pad(6f).uniform();
		}
		Score tmpScore = ScoreManager.getInstance().getCurrentLevelBestScore();
		if ( tmpScore == null || newScore.getTotalScore() > tmpScore.getTotalScore()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					ScoreManager.getInstance().saveBestScore(newScore);        				
				}
			}).start();

		    messageTable.row().expand();
		    messageTable.add(new Label(I18n.t("newRecord"), skin)).pad(6f).uniform().colspan(2);
		}
		messageTable.row().expand();
	}
}

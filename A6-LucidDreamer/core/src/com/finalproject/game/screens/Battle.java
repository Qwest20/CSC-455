package com.finalproject.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.ScreenUtils;
import com.finalproject.game.Base;
import com.finalproject.game.entities.EnemyStats;
import com.finalproject.game.menus.GameOver;
import java.util.Random;
import com.finalproject.game.menus.Victory;

// dialogue classes
class BattleStart extends Dialog{

    private final Overworld overworld;
    private final Base base;
    private final EnemyStats engagedEnemy;
    private final Stage stage;
    private final boolean melancholysTurn;

    public BattleStart(String title, Skin skin, EnemyStats eE, Base b, Overworld o, Stage s) {
        super(title, skin);
        this.base = b;
        this.overworld = o;
        this.engagedEnemy = eE;
        this.melancholysTurn = (eE.getSpeed() <= b.mS.getSpeed());
        this.stage = s;

        // output
        text("Battle Start!");
        button("==>");
    }

    @Override
    protected void result(Object object) {
        // ui stage stuffs (start with one character's turn or the other depending on speed values, and then follow the chain that they each start)
        if (melancholysTurn) {
            MelancholyTurn mT = new MelancholyTurn("Your turn!", this.getSkin(), this.base, this.overworld, this.engagedEnemy, stage);
            mT.setOriginY(base.melancholy.y);
            mT.setColor(Color.valueOf("006666"));
            mT.setWidth(1280);
            stage.addActor(mT);
        } else {
            EnemyTurn eT = new EnemyTurn("Their turn!", this.getSkin(), this.base, this.engagedEnemy, stage, this.overworld);
            eT.setOriginY(base.melancholy.y);
            eT.setColor(Color.valueOf("006666"));
            eT.setWidth(1280);
            stage.addActor(eT);
        }
        this.remove();
    }
}
class MelancholyTurn extends Dialog {

    private final Base base;
    private final Overworld overworld;
    private final EnemyStats engagedEnemy;
    private final Stage s;

    public MelancholyTurn(String title, Skin skin, Base b, Overworld o, EnemyStats e, Stage s) {
        super(title, skin);
        this.base = b;
        overworld = o;
        this.engagedEnemy = e;
        this.s = s;

        text("");
        button("Strife", "strife");
        button("Sing", "sing");
    }

    @Override
    protected void result(Object o) {
        if (o.toString().equals("strife")) {
            MelancholyStrife strife = new MelancholyStrife("Your turn!",this.getSkin(),base,overworld,engagedEnemy,s);
            strife.setOriginY(base.melancholy.y);
            strife.setColor(Color.valueOf("006666"));
            strife.setWidth(1280);
            s.addActor(strife);
        } else {
            MelancholySing sing = new MelancholySing("Your turn!",this.getSkin(),base,engagedEnemy,s, overworld);
            sing.setOriginY(base.melancholy.y);
            sing.setColor(Color.valueOf("006666"));
            sing.setWidth(1280);
            s.addActor(sing);
        }
        this.remove();
    }
}
class MelancholyStrife extends Dialog {

    private final Base base;
    private final EnemyStats engagedEnemy;
    private final Stage s;
    private final Overworld overworld;

    public MelancholyStrife(String title, Skin skin, Base b, Overworld o, EnemyStats e, Stage s) {
        super(title, skin);
        this.base = b;
        this.overworld = o;
        this.engagedEnemy = e;
        this.s = s;

        // strife action
        Random rand = new Random();
        int melAttackVal = base.mS.getAttack() + (rand.nextInt(6) - 2); // goes from mel's attack +3 to -2
        engagedEnemy.setHp(engagedEnemy.getHp() - melAttackVal);
        if(engagedEnemy.getHp() < 0){
            melAttackVal -= (Math.abs(engagedEnemy.getHp()));
            engagedEnemy.setHp(0);
        }

        text("Melancholy dealt " + melAttackVal + " damage to " + engagedEnemy.getName());
        button("==>");
    }

    // after a strife, hand this over to the enemy for their turn
    @Override
    protected void result(Object o) {
        if(engagedEnemy.getHp() <= 0){
            VictoryDia v = new VictoryDia("", this.getSkin(), this.base, this.overworld);
            v.setOriginY(base.melancholy.y);
            v.setColor(Color.valueOf("006666"));
            v.setWidth(1280);
            s.addActor(v);
        }
        else {
            EnemyTurn eT = new EnemyTurn("Their turn!", this.getSkin(), this.base, this.engagedEnemy, s, overworld);
            eT.setOriginY(base.melancholy.y);
            eT.setColor(Color.valueOf("006666"));
            eT.setWidth(1280);
            s.addActor(eT);
        }
        this.remove();
    }
}
class MelancholySing extends Dialog {

    private final Base base;
    private final EnemyStats engagedEnemy;
    private final Stage s;
    private final Overworld overworld;

    public MelancholySing(String title, Skin skin, Base b, EnemyStats e, Stage s, Overworld o) {
        super(title, skin);
        this.base = b;
        this.engagedEnemy = e;
        this.s = s;
        this.overworld = o;

        // sing action
        Random rand = new Random();
        final int melHealVal = base.mS.getHope() + (rand.nextInt(6) - 2); // goes from mel's hope +3 to -2
        base.mS.setHp(base.mS.getHp() + melHealVal);
        if (base.mS.getHp() > 100) {
            base.mS.setHp(100);
        }

        text("Melancholy healed " + melHealVal + " HP");
        button("==>");
    }

    // after a sing, hand this over to the enemy for their turn
    @Override
    protected void result(Object o) {
        EnemyTurn eT = new EnemyTurn("Their turn!", this.getSkin(), this.base, this.engagedEnemy, this.s, this.overworld);
        eT.setOriginY(base.melancholy.y);
        eT.setColor(Color.valueOf("006666"));
        eT.setWidth(1280);
        s.addActor(eT);
        this.remove();
    }
}
class EnemyTurn extends Dialog {

    private final Base base;
    private final Stage s;
    private final EnemyStats engagedEnemy;
    private final Overworld overworld;

    public EnemyTurn(String title, Skin skin, Base b, EnemyStats e, Stage s, Overworld o) {
        super(title, skin);
        this.base = b;
        this.s = s;
        this.engagedEnemy = e;
        this.overworld = o;

        // enemy attack logic
        Random rand = new Random();
        int dmg = e.getAttack() + (rand.nextInt(6) - 2); // goes from enemy's attack +3 to -2
        base.mS.setHp(base.mS.getHp() - dmg);
        if(base.mS.getHp() < 0){
            dmg -= (Math.abs(base.mS.getHp()));
            base.mS.setHp(0);
        }

        // output
        text("Enemy did " + dmg + " damage to Melancholy");
        button("==>");
    }

    @Override
    protected void result(Object o) {
        if (base.mS.getHp() <= 0) {
            Failure f = new Failure("", this.getSkin(), this.base);
            f.setOriginY(base.melancholy.y);
            f.setColor(Color.valueOf("006666"));
            f.setWidth(1280);
            s.addActor(f);
        } else {
            MelancholyTurn mT = new MelancholyTurn("Your turn!", this.getSkin(), this.base, this.overworld, this.engagedEnemy, s);
            mT.setOriginY(base.melancholy.y);
            mT.setColor(Color.valueOf("006666"));
            mT.setWidth(1280);
            s.addActor(mT);
        }
        this.remove();
    }
}
class VictoryDia extends Dialog{

    private final Base base;
    private final Overworld overworld;

    public VictoryDia(String title, Skin skin, Base b, Overworld o) {
        super(title, skin);
        this.base = b;
        this.overworld = o;

        // output
        text("You won the battle!");
        button("==>");
    }

    @Override
    protected void result(Object object) {
        Gdx.graphics.setContinuousRendering(true);
        base.battleBGMPlayer.stop();
        if(base.enemies.size() == 0)
            base.setScreen(new Victory(this.base));
        else
            base.setScreen(overworld);
    }
}
class Failure extends Dialog{

    private final Base base;

    public Failure(String title, Skin skin, Base b) {
        super(title, skin);
        this.base = b;

        // output
        text("You lose...");
        button("==>");
    }

    @Override
    protected void result(Object object) {
        Gdx.graphics.setContinuousRendering(true);
        base.battleBGMPlayer.stop();
        base.setScreen(new GameOver(base));
    }
}

public class Battle extends ScreenAdapter {

    private final EnemyStats engagedEnemy;
    private final Base base;
    private final Overworld overworld;
    private Stage s;

    public Battle(Base b, EnemyStats eE, Overworld o) {
        base = b;
        this.engagedEnemy = eE;
        overworld = o;
    }

    @Override
    public void show() {

        // start the music
        base.battleBGMPlayer.setLooping(true);
        base.battleBGMPlayer.setVolume(1);
        base.battleBGMPlayer.play();

        // update input readings on this screen in particular, although this time it will be based on a stage of button objects
        Gdx.input.setInputProcessor(s = new Stage());
        Skin uS = new Skin(Gdx.files.internal("UI/uiskin.json"));

        Gdx.graphics.setContinuousRendering(false);

        BattleStart bS = new BattleStart("", uS, engagedEnemy, base, overworld, s);
        bS.setOriginY(base.melancholy.y);
        bS.setColor(Color.valueOf("006666"));
        bS.setWidth(1280);
        s.addActor(bS);
    }

    @Override
    public void render(float delta) {

        ScreenUtils.clear(Color.valueOf("080033"));
        Texture battleMel = new Texture(Gdx.files.internal("Melancholy/NoRef/MelancholyNoRef1.png"));

        // draw battle information on screen
        base.batch.begin();
        base.myTitleFont.draw(base.batch, "BATTLE!", base.melancholy.x - 75, base.melancholy.y + 300);
        base.myFont.draw(base.batch, base.mS.getName().toUpperCase(), base.melancholy.x - 450, base.melancholy.y + 175);
        base.myFont.draw(base.batch, engagedEnemy.getName().toUpperCase(), base.melancholy.x + 375, base.melancholy.y + 175);
        base.batch.draw(battleMel, base.melancholy.x - 400, base.melancholy.y - 100);
        base.batch.draw(engagedEnemy.getSprite(), base.melancholy.x + 425, base.melancholy.y - 100);
        base.myFont.draw(base.batch, "HP: " + base.mS.getHp() + "/" + base.mS.getMaxHp(), base.melancholy.x - 450, base.melancholy.y + 125);
        base.myFont.draw(base.batch, "HP: " + engagedEnemy.getHp() + "/" + engagedEnemy.getMaxHp(), base.melancholy.x + 375, base.melancholy.y + 125);
        base.batch.end();
        s.draw();
    }

    // prevents the key behaviors from persisting into the next room
    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

    @Override
    public void dispose() {
        super.dispose();
        s.dispose();
    }
}

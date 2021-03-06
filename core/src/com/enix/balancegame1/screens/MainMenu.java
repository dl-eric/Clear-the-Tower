package com.enix.balancegame1.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Scaling;
import com.enix.balancegame1.Play;

/**
 * Created by Eric on 5/26/2015.
 */
public class MainMenu implements Screen {

    private Stage stage;
    private TextureAtlas atlas;
    private Skin skin;
    private Table table;

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(255, 255, 255, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height)
    {
        stage.getViewport().update(width, height, true);
        table.invalidateHierarchy();
        table.setSize(width, height);
    }

    @Override
    public void show()
    {
        stage = new Stage();
        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);

        table = new Table(skin);
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        //Buttons----------------------------------------------------------------------------------

        //Exit Button
        TextButton buttonExit = new TextButton("Exit", skin);
        buttonExit.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ExitDialog exitDialog = new ExitDialog("Confirm Exit", skin); // TODO

                exitDialog.show(stage);
            }
        });
        buttonExit.pad(20);

        //Play Button
        TextButton buttonPlay = new TextButton("Play", skin);
        buttonPlay.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game)Gdx.app.getApplicationListener()).setScreen(new Play());
            }
        });
        buttonPlay.pad(20);

        //Heading
        Image banner = new Image(new Texture("img/banner.png"));

        //Putting stuff together
        table.add(banner).width(900).height(500);
        table.getCell(banner).spaceBottom(100);
        table.row();
        table.add(buttonPlay).width(600).height(300);
        table.row();
        table.getCell(buttonPlay).spaceBottom(20);
        table.add(buttonExit).width(600).height(300);
        table.debug();          //TODO REMOVE LATER
        stage.addActor(table);
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void dispose()
    {
        stage.dispose();
        atlas.dispose();
        skin.dispose();
    }

    @Override
    public void resume()
    {

    }

    @Override
    public void hide()
    {
        Gdx.input.setInputProcessor(null);
    }
}

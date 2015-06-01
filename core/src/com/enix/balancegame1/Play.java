package com.enix.balancegame1;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.enix.balancegame1.com.enix.balancegame1.entities.Rocket;


/**
 * Created by Eric on 5/26/2015.
 */
public class Play implements Screen {

    private World world;
    private Box2DDebugRenderer debugRenderer;
    private SpriteBatch batch;
    private OrthographicCamera camera;

    private final float TIMESTEP = 1 / 60f;
    private final int VELOCITYITERATIONS = 8;
    private final int POSITIONITERATION = 3;

    private BitmapFont white;
    private Label fuelLabel;
    private Label scoreLabel;

    private Body body;
    private Rocket rocket;
    private int score;

    private MeterGenerator meterGenerator;

    private Array<Body> temporaryBodies = new Array<Body>();

    private Stage stage;

    private Table table;
    private Skin skin;
    private TextureAtlas atlas;

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClearColor(156/255f, 198/255f, 253/255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rocket.update();
        world.step(TIMESTEP, VELOCITYITERATIONS, POSITIONITERATION);

        camera.position.x = rocket.getBody().getPosition().x > camera.position.x ? rocket.getBody().getPosition().x : camera.position.x;
        camera.position.y = rocket.getBody().getPosition().y + 4;
        camera.update();

        stage.draw();
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        world.getBodies(temporaryBodies);
        for(Body body : temporaryBodies)
            if(body.getUserData() != null && body.getUserData() instanceof Sprite)
            {
                Sprite sprite = (Sprite) body.getUserData();
                sprite.setPosition(body.getPosition().x - sprite.getWidth() / 2, body.getPosition().y - sprite.getHeight() / 2);
                sprite.setRotation(body.getAngle() * MathUtils.radiansToDegrees);
                sprite.draw(batch);
            }
        //stage.draw();
        fuelLabel.setText("Fuel: " + Integer.toString(rocket.getFuel()));
        scoreLabel.setText("Distance: " + calculateScore((int) (Intersector.distanceLinePoint(0, 7, Gdx.graphics.getWidth(), 7, rocket.getBody().getWorldCenter().x, rocket.getBody().getWorldCenter().y))));
        batch.end();

        debugRenderer.render(world, camera.combined);

        meterGenerator.generate(camera.position.y + camera.viewportHeight / 2);
    }

    public int calculateScore(int distance)
    {
        if(distance > score)
        {
            score = distance;
            return score;
        }
        else
            return score;
    }

    @Override
    public void resize(int width, int height) //Meter to pixel ratio: 80:1
    {
        camera.viewportWidth = width / 80;
        camera.viewportHeight = height / 80;
        camera.update();
    }

    @Override
    public void show()
    {
        world = new World(new Vector2(0, -9.81f), false);
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        stage = new Stage();

        table = new Table(new Skin(new TextureAtlas("ui/button.pack")));
        table.setBounds(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        camera = new OrthographicCamera(Gdx.graphics.getWidth() / 80, Gdx.graphics.getHeight() / 80);

        //Rocket
        rocket = new Rocket(world, 0, 8, 8f);

        world.setContactListener(rocket);

        Gdx.input.setInputProcessor(new InputMultiplexer(new InputAdapter()
        {
            @Override
            public boolean touchDown(int screenX, int screenY, int pointer, int button)
            {
                if(rocket.getFuel() > 0)
                {
                    rocket.getBody().applyLinearImpulse(0, 300, rocket.getBody().getWorldCenter().x, rocket.getBody().getWorldCenter().y, true);
                    rocket.setFuel(rocket.getFuel() - 1);
                }
               return true;
            }
        }, rocket));

        atlas = new TextureAtlas("ui/button.pack");
        skin = new Skin(Gdx.files.internal("ui/menuSkin.json"), atlas);


        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        //Ground
        //Body Definition
        bodyDef.type = BodyType.StaticBody;
        bodyDef.position.set(0, -5);

        //Ground Shape
        ChainShape groundShape = new ChainShape();
        Vector3 botLeft = new Vector3(0, 0, 0), botRight = new Vector3(Gdx.graphics.getWidth(), 0, 0);

        camera.unproject(botLeft);
        camera.unproject(botRight);

        groundShape.createChain(new float[] {botLeft.x, botLeft.y, botRight.x, botRight.y});

        //Fixture Definition
        fixtureDef.shape = groundShape;
        fixtureDef.friction = 0.5f;
        fixtureDef.restitution = 0;

        body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        groundShape.dispose();

        //Fuel Indicator
        fuelLabel = new Label("Fuel: " + Integer.toString(rocket.getFuel()), skin);

        //Independent Window
        final Window pause = new Window("Pause", skin); // TODO
        TextButton continueButton = new TextButton("Continue", skin);
        continueButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause.setVisible(false);
            }
        });

        pause.padTop(64);
        pause.row();
        pause.add(continueButton).width(600);
        pause.row();
        pause.add(new TextButton("Restart", skin)).width(600);
        pause.setSize(stage.getWidth() / 2, stage.getHeight() / 4);
        pause.setPosition(stage.getWidth() / 2 - pause.getWidth() / 2, stage.getHeight() / 2 - pause.getHeight() / 2);

        Button pauseButton = new Button(skin);
        pauseButton.pad(10);
        pauseButton.addListener(new ClickListener()
        {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                pause.setVisible(true);
                Gdx.app.exit();
                //(Gdx.app.getApplicationListener()).pause();
            }
        });

        //Score
        scoreLabel = new Label("Distance: " + (int)(Intersector.distanceLinePoint(0, 0, Gdx.graphics.getWidth(), 0, rocket.getBody().getWorldCenter().x, rocket.getBody().getWorldCenter().y)), skin);

        table.add(fuelLabel).top().left().padTop(5).padLeft(20).padRight(20);
        table.add(pauseButton).top().right().padTop(5).padLeft(20).padRight(20).width(100).height(100);
        table.row();
        table.add(scoreLabel).top().left().padTop(5).padLeft(20).expand();
        table.debug();

        stage.addActor(table);
        stage.addActor(pause);
        pause.setVisible(false);

        meterGenerator = new MeterGenerator(body, botLeft.x, botRight.x, 1, 1);
    }

    @Override
    public void hide()
    {
        dispose();
    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    @Override
    public void dispose()
    {
        world.dispose();
        debugRenderer.dispose();
        batch.dispose();
    }
}

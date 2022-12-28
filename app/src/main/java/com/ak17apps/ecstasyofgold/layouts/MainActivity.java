package com.ak17apps.ecstasyofgold.layouts;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.ak17apps.ecstasyofgold.R;
import com.ak17apps.ecstasyofgold.utils.Generator;
import com.ak17apps.ecstasyofgold.utils.MyService;
import com.ak17apps.ecstasyofgold.utils.ServiceCallbacks;
import com.ak17apps.ecstasyofgold.utils.Settings;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity implements ServiceCallbacks {
    private AppCompatActivity activity = this;
    private int boardWidth;
    private int boardHeight;
    private Player[] players;
    private Center.Coffin[] coffins;
    private boolean[][] firingPowerKnowings;    //[who] knows [whose] firing power
    private LinearLayout buttonsLayout;
    private LinearLayout activityLayout;
    private MyService myService;
    private boolean bound = false;
    private int nextPlayer;
    private int currentPlayer;
    private TextView instructionTV;
    private TextView viewPowerTV;
    private TextView increaseLifeTV;
    private TextView swapFiringPowerTV;
    private TextView shootTV;
    private TextView getToolTV;
    private TextView buySellToolTV;
    private TextView inventoryTV;
    private TextView openCoffinTV;
    private ScrollView noteSC;
    private TextView noteTV;
    private List<String> noteList;
    private boolean swapFiringPowerAction;
    private boolean shootAction;
    private boolean openCoffinAction;
    private int goldOwnerIndex;
    private int goldOwnerTurn;

    @SuppressLint({"SourceLockedOrientationActivity", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        DisplayMetrics metrics = new DisplayMetrics();
        ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);
        boardWidth = metrics.widthPixels;
        boardHeight = (int) (metrics.heightPixels * 0.7);

        setContentView(R.layout.activity_main);
        activityLayout = findViewById(R.id.activityLayout);
        instructionTV = findViewById(R.id.instructionTV);
        RelativeLayout mainLayout = findViewById(R.id.mainLayout);

        List<String> names = Generator.getNames();
        List<Integer> firingPowers = Generator.getFiringPowers();
        players = new Player[6];
        coffins = new Center.Coffin[5];
        firingPowerKnowings = new boolean[6][6];

        //bottom, user
        Player player = new Player(names.get(0), firingPowers.get(0), 3, 0);
        player.setPlayerId(0);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (boardWidth - player.getLayoutWidth()) / 2;
        params.topMargin = boardHeight - player.getLayoutHeight();
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        //top player npc
        player = new Player(names.get(1), firingPowers.get(1), 3, 0);
        player.setPlayerId(3);
        params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (boardWidth - player.getLayoutWidth()) / 2;
        params.topMargin = boardHeight / 10;
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        //left top player npc
        player = new Player(names.get(2), firingPowers.get(2), 3, 0);
        player.setPlayerId(2);
        params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (boardWidth - player.getLayoutWidth()) / 10;
        params.topMargin = boardHeight / 4;
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        //left bottom player npc
        player = new Player(names.get(3), firingPowers.get(3), 3, 0);
        player.setPlayerId(1);
        params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (boardWidth - player.getLayoutWidth()) / 10;
        params.topMargin = (int) (boardHeight / 1.5);
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        //right top player npc
        player = new Player(names.get(4), firingPowers.get(4), 3, 0);
        player.setPlayerId(4);
        params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (int) ((boardWidth - player.getLayoutWidth()) / 1.1);
        params.topMargin = boardHeight / 4;
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        //right bottom player npc
        player = new Player(names.get(5), firingPowers.get(5), 3, 0);
        player.setPlayerId(5);
        params = new RelativeLayout.LayoutParams(player.getLayoutWidth(), player.getLayoutHeight());
        params.leftMargin = (int) ((boardWidth - player.getLayoutWidth()) / 1.1);
        params.topMargin = (int) (boardHeight / 1.5);
        mainLayout.addView(player, params);
        players[player.getPlayerId()] = player;

        Center center = new Center(this);
        params = new RelativeLayout.LayoutParams(center.getLayoutWidth(), center.getLayoutHeight());
        params.leftMargin = (boardWidth - center.getLayoutWidth()) / 2;
        params.topMargin = (boardHeight - center.getLayoutHeight()) / 2;
        mainLayout.addView(center, params);

        buttonsLayout = findViewById(R.id.buttonsLayout);
        noteSC = findViewById(R.id.sc);
        LinearLayout.LayoutParams par = new LinearLayout.LayoutParams(boardWidth / 3, boardHeight / 4);
        noteSC.setLayoutParams(par);

        LinearLayout scLL = findViewById(R.id.scLL);
        scLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings.fullScreenHistoryWindow(activityLayout, activity, noteList);
            }
        });

        noteTV = findViewById(R.id.scNote);
        noteList = new ArrayList<>();

        viewPowerTV = findViewById(R.id.viewPowerTV);
        viewPowerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPower();
            }
        });

        increaseLifeTV = findViewById(R.id.increaseLifeTV);
        increaseLifeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseLife();
            }
        });

        swapFiringPowerTV = findViewById(R.id.swapFiringPowerTV);
        swapFiringPowerTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                swapFiringPower();
            }
        });

        shootTV = findViewById(R.id.shootTV);
        shootTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shoot();
            }
        });

        getToolTV = findViewById(R.id.getToolTV);
        getToolTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTool();
            }
        });

        buySellToolTV = findViewById(R.id.buySellToolTV);
        buySellToolTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBuySellWindow();
            }
        });

        inventoryTV = findViewById(R.id.inventoryTV);
        inventoryTV.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showInventory();
            }
        });

        openCoffinTV = findViewById(R.id.openCoffinTV);
        openCoffinTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCoffin();
            }
        });

        swapFiringPowerTV.setVisibility(View.GONE);
        shootTV.setVisibility(View.GONE);
        getToolTV.setVisibility(View.GONE);
        viewPowerTV.setVisibility(View.GONE);
        increaseLifeTV.setVisibility(View.GONE);
        buySellToolTV.setVisibility(View.GONE);
        inventoryTV.setVisibility(View.GONE);
        openCoffinTV.setVisibility(View.GONE);

        nextPlayer = -1;
        currentPlayer = -2;
        goldOwnerIndex = -1;
        goldOwnerTurn = 0;

        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler(){
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                showText(e);
            }
        });
    }

    private void showText(final Throwable e){
        final Thread thread = new Thread() {
            public void run() {
                Looper.prepare();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        StringWriter errors = new StringWriter();
                        e.printStackTrace(new PrintWriter(errors));

                        Settings.errorWindow(activityLayout, activity, errors.toString());
                    }
                }, 0);

                Looper.loop();
            }
        };
        thread.start();
    }

    private void viewPower(){
        firingPowerKnowings[0][0] = true;
        players[0].updateFiringPowerShowing();
        noteList.add(getResources().getString(R.string.viewed_firing_power));
        players[5].hideLastAct();
        players[0].showLastAct();
        nextPlayer++;
        manageTurn();
    }

    private void increaseLife(){
        players[0].setLives(players[0].getLives() + 1);
        noteList.add(getResources().getString(R.string.increase__life));
        players[5].hideLastAct();
        players[0].showLastAct();
        nextPlayer++;
        manageTurn();
    }

    private void swapFiringPower(){
        instructionTV.setText(getResources().getString(R.string.tap_on_an_opponent_to_swap_with));
        allButtonsGone();
        swapFiringPowerAction = true;
    }

    private void handleFiringPowerSwap(int playerId){
        int tmpFiringPower = players[playerId].getFiringPower();
        players[playerId].setFiringPower(players[0].getFiringPower());
        players[0].setFiringPower(tmpFiringPower);

        boolean tmpFiringPowerKnowing = firingPowerKnowings[0][playerId];
        firingPowerKnowings[0][playerId] = firingPowerKnowings[0][0];
        firingPowerKnowings[0][0] = tmpFiringPowerKnowing;
        players[0].updateFiringPowerShowing();
        players[playerId].updateFiringPowerShowing();

        noteList.add(getResources().getString(R.string.swap__firing_power) + " " + players[playerId].getNameTV().getText());
        players[5].hideLastAct();
        players[0].showLastAct();

        swapFiringPowerAction = false;
        instructionTV.setText("");
        nextPlayer++;
        manageTurn();
    }

    private void shoot(){
        instructionTV.setText(getResources().getString(R.string.tap_on_an_unprotected_opponent_to_shoot));
        allButtonsGone();
        shootAction = true;
    }

    private void handleShoot(int shooterPlayerId, int targetPlayerId){
        int decreaseLife = 0;
        int showPower = 0;

        if(firingPowerKnowings[targetPlayerId][targetPlayerId] && firingPowerKnowings[targetPlayerId][shooterPlayerId]){
            if(players[shooterPlayerId].getFiringPower() > players[targetPlayerId].getFiringPower()){
                finalizeShoot(shooterPlayerId, targetPlayerId, true);
                return;
            }else{
                finalizeShoot(shooterPlayerId, targetPlayerId, false);
                return;
            }
        }

        if(players[targetPlayerId].getLives() < 2){
            finalizeShoot(shooterPlayerId, targetPlayerId, false);
            return;
        }

        if(firingPowerKnowings[targetPlayerId][targetPlayerId] && players[targetPlayerId].getFiringPower() > 5){
            showPower++;
        }
        if(firingPowerKnowings[targetPlayerId][shooterPlayerId] && players[shooterPlayerId].getFiringPower() < 6){
            showPower++;
        }

        if(players[targetPlayerId].getLives() >= 2 && players[targetPlayerId].getLives() < 6){
            decreaseLife++;
        }else{
            showPower++;
        }
        if(players[targetPlayerId].getSpades() > 0 || players[targetPlayerId].getMap() > 0 || players[targetPlayerId].getWater() > 0){
            decreaseLife++;
        }
        if(players[targetPlayerId].getSpades() > 0 && players[targetPlayerId].getMap() > 0 && players[targetPlayerId].getWater() > 0){
            decreaseLife++;
        }

        float decreaseLifeP = decreaseLife / 3F;
        float showPowerP = showPower / 3F;

        if(decreaseLifeP > showPowerP){
            finalizeShoot(shooterPlayerId, targetPlayerId, true);
        }else if(showPowerP > decreaseLifeP){
            finalizeShoot(shooterPlayerId, targetPlayerId, false);
        }else{
            if(new Random().nextInt(2) == 0){
                finalizeShoot(shooterPlayerId, targetPlayerId, true);
            }else{
                finalizeShoot(shooterPlayerId, targetPlayerId, false);
            }
        }
    }

    private void finalizeShoot(int shooterPlayerId, int targetPlayerId, boolean decreaseLife){
        noteList.add(getResources().getString(R.string.shoots_at) + " " + players[targetPlayerId].getNameTV().getText());
        players[shooterPlayerId == 0 ? 5 : shooterPlayerId - 1].hideLastAct();
        players[shooterPlayerId].showLastAct();

        if(decreaseLife){
            players[targetPlayerId].setLives(players[targetPlayerId].getLives() - 1);
        }else{
            for(int i = 0; i < firingPowerKnowings.length; i++) {
                firingPowerKnowings[i][targetPlayerId] = true;
                firingPowerKnowings[i][shooterPlayerId] = true;
            }
            players[shooterPlayerId].updateFiringPowerShowing();
            players[targetPlayerId].updateFiringPowerShowing();

            if(players[shooterPlayerId].getFiringPower() > players[targetPlayerId].getFiringPower()){
                players[targetPlayerId].setLives(players[targetPlayerId].getLives() - 2);
            }
        }

        if(players[targetPlayerId].getLives() <= 0){
            players[targetPlayerId].setLives(0);
            players[shooterPlayerId].setMap(players[shooterPlayerId].getMap() + players[targetPlayerId].getMap());
            players[targetPlayerId].setMap(0);
            players[shooterPlayerId].setWater(players[shooterPlayerId].getWater() + players[targetPlayerId].getWater());
            players[targetPlayerId].setWater(0);
            players[shooterPlayerId].setSpades(players[shooterPlayerId].getSpades() + players[targetPlayerId].getSpades());
            players[targetPlayerId].setSpades(0);
            if(players[targetPlayerId].isGold()){
                players[shooterPlayerId].setGold(true);
                players[targetPlayerId].setGold(false);
                goldOwnerIndex = shooterPlayerId;
                goldOwnerTurn = 0;
            }

            noteList.add(players[targetPlayerId].getNameTV().getText() + " " + getResources().getString(R.string.dead) + ". " +
                    players[shooterPlayerId].getNameTV().getText() + " " + getResources().getString(R.string.received_all_their_tools));
        }

        if(shooterPlayerId == 0) {
            shootAction = false;
            instructionTV.setText("");
            nextPlayer++;
            manageTurn();
        }
    }

    private void getTool(){
        Player player = players[0];
        String tool = "";

        switch(new Random().nextInt(3)){
            case 0:
                player.setSpades(player.getSpades() + 1);
                tool = getResources().getString(R.string.spade);
                break;
            case 1:
                player.setMap(player.getMap() + 1);
                tool = getResources().getString(R.string.map);
                break;
            default:
                player.setWater(player.getWater() + 1);
                tool = getResources().getString(R.string.water);
                break;
        }

        noteList.add(getResources().getString(R.string.got_one_more) + " " + tool);
        players[5].hideLastAct();
        players[0].showLastAct();
        nextPlayer++;
        manageTurn();
    }

    private void openBuySellWindow(){
        Settings.buySellWindow(activityLayout, this, getApplicationContext(), players[0]);
    }

    public void buySellTool(Player player){
        players[0] = player;
        noteList.add(getResources().getString(R.string.spade) + ": " + player.getSpades() + ", " +
                getResources().getString(R.string.map) + ": " + player.getMap() + ", " +
                getResources().getString(R.string.water) + ": " + player.getWater());
        players[5].hideLastAct();
        players[0].showLastAct();
        nextPlayer++;
        manageTurn();
    }

    private void showInventory(){
        Settings.inventoryWindow(activityLayout, this, getApplicationContext(), players[0]);
    }

    private void openCoffin(){
        instructionTV.setText(getResources().getString(R.string.tap_on_an_unopened_coffin_to_open));
        openCoffinAction = true;
    }

    private void handleCoffinOpening(int coffinId){
        Center.Coffin c = coffins[coffinId];
        c.setBackgroundResource(R.drawable.open_coffin);
        c.setOpen(true);

        Player p = players[0];
        String gold = getResources().getString(R.string.you_have_opened_a_coffin);

        if(c.isGold()){
            p.setGold(true);
            goldOwnerIndex = 0;
            goldOwnerTurn = 0;
            gold = gold + " " + getResources().getString(R.string.and_found_the_gold);
        }

        p.setSpades(p.getSpades() - 1);
        p.setWater(p.getWater() - 1);
        p.setMap(p.getMap() - 1);

        openCoffinAction = false;
        instructionTV.setText("");
        noteList.add(gold + ".");
        players[5].hideLastAct();
        players[0].showLastAct();
        nextPlayer++;
        manageTurn();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, MyService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unbindService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService();
    }

    private void unbindService(){
        if (bound) {
            myService.setCallbacks(null);
            unbindService(serviceConnection);
            bound = false;
        }
    }

    private void manageTurn(){
        if(currentPlayer == nextPlayer){
            return;
        }

        if(nextPlayer < 0){
            nextPlayer = new Random().nextInt(6);
        }else if(nextPlayer > 5){
            nextPlayer = 0;
        }

        if(goldOwnerIndex != -1){
            goldOwnerTurn++;
        }
        if(goldOwnerTurn >= 6){
            Toast.makeText(getApplicationContext(), getResources().getString(R.string.winner) + ": " + players[goldOwnerIndex].getNameTV().getText(), Toast.LENGTH_SHORT).show();
            noteList.add(players[goldOwnerIndex].getNameTV().getText() + " " + getResources().getString(R.string.wins_the_game));
            allButtonsGone();
            myService.stopRunnable();
            return;
        }

        noteList.add(players[nextPlayer].getNameTV().getText() + "'s turn:");

        if(players[nextPlayer].getProtection() > 0){
            players[nextPlayer].setProtection(players[nextPlayer].getProtection() - 1);
        }

        if(nextPlayer == 0){    //user
            swapFiringPowerTV.setVisibility(View.VISIBLE);
            getToolTV.setVisibility(View.VISIBLE);
            inventoryTV.setVisibility(View.VISIBLE);

            boolean everyoneProtected = true;
            for(int i = 0; i < players.length && everyoneProtected; i++){
                if(players[i].getProtection() == 0){
                    everyoneProtected = false;
                }
            }
            if(everyoneProtected){
                shootTV.setVisibility(View.GONE);
            }else{
                shootTV.setVisibility(View.VISIBLE);
            }

            Player player = players[0];
            if (!firingPowerKnowings[0][0]){
                viewPowerTV.setVisibility(View.VISIBLE);
            } else {
                viewPowerTV.setVisibility(View.GONE);
            }

            if(player.getLives() < 10){
                increaseLifeTV.setVisibility(View.VISIBLE);
            }else{
                increaseLifeTV.setVisibility(View.GONE);
            }

            if(player.getSpades() + player.getWater() + player.getMap() > 1) {
                buySellToolTV.setVisibility(View.VISIBLE);
            }else{
                buySellToolTV.setVisibility(View.GONE);
            }

            if(player.getSpades() > 0 && player.getWater() > 0 && player.getMap() > 0){
                boolean allOpen = true;
                for(int i = 0; i < coffins.length; i++){
                    if(!coffins[i].isOpen()){
                        allOpen = false;
                        break;
                    }
                }

                openCoffinTV.setVisibility(allOpen ? View.GONE : View.VISIBLE);
            }else{
                openCoffinTV.setVisibility(View.GONE);
            }

            currentPlayer = nextPlayer;
            updateUserNotes();
            return;
        }else {     //opponent
            allButtonsGone();
            chooseAI(players[nextPlayer]);
        }
    }

    private void continueWithNextPlayer(){
        if(nextPlayer == 2){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
        }

        if(nextPlayer > 5){
            nextPlayer = 0;
        }

        players[nextPlayer == 0 ? 5 : nextPlayer - 1].hideLastAct();
        players[nextPlayer].showLastAct();
        currentPlayer = nextPlayer;
        nextPlayer++;
        updateOpponentNotes();
    }

    private void allButtonsGone(){
        swapFiringPowerTV.setVisibility(View.GONE);
        shootTV.setVisibility(View.GONE);
        getToolTV.setVisibility(View.GONE);
        viewPowerTV.setVisibility(View.GONE);
        increaseLifeTV.setVisibility(View.GONE);
        buySellToolTV.setVisibility(View.GONE);
        inventoryTV.setVisibility(View.GONE);
        openCoffinTV.setVisibility(View.GONE);
    }

    private void chooseAI(Player player){
        boolean goldInCoffin = false;
        for(int i = 0; i < coffins.length; i++){
            if(coffins[i].isGold() && !coffins[i].isOpen()){
                goldInCoffin = true;
                break;
            }
        }

        if(new Random().nextInt(2) == 0){
            act(player, goldInCoffin);
        }else{
            randomAct(player, goldInCoffin);
        }
    }

    private void act(Player player, boolean goldInCoffin){
        Resources r = getResources();

        if(player.getLives() == 0){
            player.setLives(1);
            noteList.add(r.getString(R.string.increased_life));
            continueWithNextPlayer();
        }

        if(goldInCoffin) {
            if (player.getSpades() > 0 && player.getMap() > 0 && player.getWater() > 0) {
                if (player.getLives() > 5) {
                    if (firingPowerKnowings[player.getPlayerId()][player.getPlayerId()]) {
                        int nrOfKnownFiringPower = 0;
                        int sumFiringPower = 0;

                        for (int i = 0; i < firingPowerKnowings.length; i++) {
                            if (firingPowerKnowings[player.getPlayerId()][i]) {
                                nrOfKnownFiringPower++;
                                sumFiringPower += players[i].getFiringPower();
                            }
                        }

                        if (nrOfKnownFiringPower >= 3) {
                            if(player.getFiringPower() > ((float) sumFiringPower / nrOfKnownFiringPower)) {
                                List<Integer> closedCoffinIds = new ArrayList<>();
                                for (int i = 0; i < coffins.length; i++) {
                                    if (!coffins[i].isOpen()) {
                                        closedCoffinIds.add(coffins[i].getCoffinId());
                                    }
                                }

                                Center.Coffin c = coffins[closedCoffinIds.get(new Random().nextInt(closedCoffinIds.size()))];
                                c.setBackgroundResource(R.drawable.open_coffin);
                                c.setOpen(true);

                                String gold = r.getString(R.string.opened_a_coffin);

                                if (c.isGold()) {
                                    player.setGold(true);
                                    goldOwnerIndex = player.getPlayerId();
                                    goldOwnerTurn = 0;
                                    gold = gold + " " + r.getString(R.string.and_found_the_gold);
                                }

                                player.setSpades(player.getSpades() - 1);
                                player.setWater(player.getWater() - 1);
                                player.setMap(player.getMap() - 1);
                                noteList.add(gold);
                                continueWithNextPlayer();
                            }else{
                                int highestFiringPowerOpponentId = player.getPlayerId();
                                int maxFiringPower = 0;

                                for(int i = 0; i < firingPowerKnowings.length; i++){
                                    if(firingPowerKnowings[player.getPlayerId()][i] && players[i].getFiringPower() > maxFiringPower){
                                        maxFiringPower = players[i].getFiringPower();
                                        highestFiringPowerOpponentId = i;
                                    }
                                }

                                int tmpFiringPower = players[highestFiringPowerOpponentId].getFiringPower();
                                players[highestFiringPowerOpponentId].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId];
                                firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                players[player.getPlayerId()].updateFiringPowerShowing();
                                players[highestFiringPowerOpponentId].updateFiringPowerShowing();

                                noteList.add(r.getString(R.string.swap__firing_power) + " " + players[highestFiringPowerOpponentId].getNameTV().getText());
                                continueWithNextPlayer();
                            }
                        }else{
                            int highestFiringPowerOpponentId = player.getPlayerId();
                            int lowestFiringPowerOpponentId = -1;
                            int unknownFiringPowerOpponentId = -1;
                            List<Integer> unknownFiringPowerOpponentIds = new ArrayList<>();
                            int maxFiringPower = 0;
                            int minFiringPower = 11;

                            for(int i = 0; i < firingPowerKnowings.length; i++){
                                if(firingPowerKnowings[player.getPlayerId()][i]){
                                    if(players[i].getFiringPower() > maxFiringPower) {
                                        maxFiringPower = players[i].getFiringPower();
                                        highestFiringPowerOpponentId = i;
                                    }
                                    if(players[i].getFiringPower() < minFiringPower && i != player.getPlayerId() && players[i].getProtection() == 0){
                                        minFiringPower = players[i].getFiringPower();
                                        lowestFiringPowerOpponentId = i;
                                    }
                                }else{
                                    unknownFiringPowerOpponentIds.add(i);
                                }
                            }

                            if(unknownFiringPowerOpponentIds.size() != 0){
                                unknownFiringPowerOpponentId = unknownFiringPowerOpponentIds.get(new Random().nextInt(unknownFiringPowerOpponentIds.size()));
                            }

                            if(highestFiringPowerOpponentId == player.getPlayerId() && unknownFiringPowerOpponentId != -1){
                                if(player.getFiringPower() <= 5){
                                    int tmpFiringPower = players[unknownFiringPowerOpponentId].getFiringPower();
                                    players[unknownFiringPowerOpponentId].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                    players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                    boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][unknownFiringPowerOpponentId];
                                    firingPowerKnowings[player.getPlayerId()][unknownFiringPowerOpponentId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                    firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                    players[player.getPlayerId()].updateFiringPowerShowing();
                                    players[unknownFiringPowerOpponentId].updateFiringPowerShowing();

                                    noteList.add(r.getString(R.string.swap__firing_power) + " " + players[unknownFiringPowerOpponentId].getNameTV().getText());
                                    continueWithNextPlayer();
                                }else{
                                    if(lowestFiringPowerOpponentId == 0){
                                        Settings.shootChooseWindow(activityLayout, this, player);
                                        myService.stopRunnable();
                                    }else if(lowestFiringPowerOpponentId != -1){
                                        handleShoot(player.getPlayerId(), lowestFiringPowerOpponentId);
                                        continueWithNextPlayer();
                                    }else{
                                        player.setLives(player.getLives() + 1);
                                        noteList.add(r.getString(R.string.increased_life));
                                        continueWithNextPlayer();
                                    }
                                }
                            }else if(highestFiringPowerOpponentId != player.getPlayerId()){
                                int tmpFiringPower = players[highestFiringPowerOpponentId].getFiringPower();
                                players[highestFiringPowerOpponentId].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId];
                                firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                players[player.getPlayerId()].updateFiringPowerShowing();
                                players[highestFiringPowerOpponentId].updateFiringPowerShowing();

                                noteList.add(r.getString(R.string.swap__firing_power) + " " + players[highestFiringPowerOpponentId].getNameTV().getText());
                                continueWithNextPlayer();
                            }
                        }
                    }else{
                        firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = true;
                        player.updateFiringPowerShowing();
                        noteList.add(r.getString(R.string.viewed_firing_power));
                        continueWithNextPlayer();
                    }
                }else{
                    player.setLives(player.getLives() + 1);
                    noteList.add(r.getString(R.string.increased_life));
                    continueWithNextPlayer();
                }
            }else{
                if(player.getSpades() > 1 && player.getWater() > 1){
                    player.setSpades(player.getSpades() - 1);
                    player.setWater(player.getWater() - 1);
                    player.setMap(1);
                    noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.spade) + ", " + r.getString(R.string.water) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                    continueWithNextPlayer();
                }else if(player.getSpades() > 1 && player.getMap() > 1){
                    player.setSpades(player.getSpades() - 1);
                    player.setMap(player.getMap() - 1);
                    player.setWater(1);
                    noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.spade) + ", " + r.getString(R.string.map) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                    continueWithNextPlayer();
                }else if(player.getWater() > 1 && player.getMap() > 1){
                    player.setWater(player.getWater() - 1);
                    player.setMap(player.getMap() - 1);
                    player.setSpades(1);
                    noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.water) + ", " + r.getString(R.string.map) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                    continueWithNextPlayer();
                }else if(player.getSpades() > 2){
                    player.setSpades(player.getSpades() - 2);
                    if(player.getMap() == 0){
                        player.setMap(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.spade) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                        continueWithNextPlayer();
                    }else{
                        player.setWater(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.spade) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                        continueWithNextPlayer();
                    }
                }else if(player.getWater() > 2){
                    player.setWater(player.getWater() - 2);
                    if(player.getMap() == 0){
                        player.setMap(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.water) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                        continueWithNextPlayer();
                    }else{
                        player.setSpades(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.water) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                        continueWithNextPlayer();
                    }
                }else if(player.getMap() > 2){
                    player.setMap(player.getMap() - 2);
                    if(player.getWater() == 0){
                        player.setWater(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.map) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                        continueWithNextPlayer();
                    }else{
                        player.setSpades(1);
                        noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.map) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                        continueWithNextPlayer();
                    }
                }else{
                    switch(new Random().nextInt(3)){
                        case 0:
                            player.setSpades(player.getSpades() + 1);
                            break;
                        case 1:
                            player.setMap(player.getMap() + 1);
                            break;
                        default:
                            player.setWater(player.getWater() + 1);
                            break;
                    }

                    noteList.add(r.getString(R.string.got_one_more) + " " + r.getString(R.string.tool));
                    continueWithNextPlayer();
                }
            }
        }else{
            //gold is out of coffin
            if(player.isGold()){
                int nrOfKnownFiringPower = 0;
                int sumFiringPower = 0;
                int maxFiringPower = 0;
                int highestFiringPowerOpponentId = -1;

                for (int i = 0; i < firingPowerKnowings.length; i++) {
                    if (firingPowerKnowings[player.getPlayerId()][i]) {
                        nrOfKnownFiringPower++;
                        sumFiringPower += players[i].getFiringPower();

                        if(players[i].getFiringPower() > maxFiringPower) {
                            maxFiringPower = players[i].getFiringPower();
                            highestFiringPowerOpponentId = i;
                        }
                    }
                }

                if(firingPowerKnowings[player.getPlayerId()][player.getPlayerId()]) {
                    float firingPowerDifferenceFromAvg;
                    if (nrOfKnownFiringPower >= 3) {
                        firingPowerDifferenceFromAvg = player.getFiringPower() - ((float) sumFiringPower / nrOfKnownFiringPower);
                    }else{
                        firingPowerDifferenceFromAvg = player.getFiringPower() - 5.5f;
                    }

                    if(highestFiringPowerOpponentId != -1 && firingPowerDifferenceFromAvg < player.getLives()){
                        int tmpFiringPower = players[highestFiringPowerOpponentId].getFiringPower();
                        players[highestFiringPowerOpponentId].setFiringPower(players[player.getPlayerId()].getFiringPower());
                        players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                        boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId];
                        firingPowerKnowings[player.getPlayerId()][highestFiringPowerOpponentId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                        firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                        players[player.getPlayerId()].updateFiringPowerShowing();
                        players[highestFiringPowerOpponentId].updateFiringPowerShowing();

                        noteList.add(r.getString(R.string.swap__firing_power) + " " + players[highestFiringPowerOpponentId].getNameTV().getText());
                        continueWithNextPlayer();
                    }else{
                        player.setLives(player.getLives() + 1);
                        noteList.add(r.getString(R.string.increased_life));
                        continueWithNextPlayer();
                    }
                }else{
                    firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = true;
                    player.updateFiringPowerShowing();
                    noteList.add(r.getString(R.string.viewed_firing_power));
                    continueWithNextPlayer();
                }
            }else {
                //gold at opponent
                if (firingPowerKnowings[player.getPlayerId()][player.getPlayerId()]) {
                    if (firingPowerKnowings[player.getPlayerId()][goldOwnerIndex]) {
                        int oneOfNextPlayersId = -1;
                        int lowestPower = 11;
                        boolean goldOwnerReached = false;
                        int i = player.getPlayerId() + 1;

                        if(i == 6){
                            i = 0;
                        }
                        if(i == goldOwnerIndex){
                            goldOwnerReached = true;
                        }

                        while(!goldOwnerReached){
                            if(firingPowerKnowings[player.getPlayerId()][i] && players[i].getFiringPower() < lowestPower){
                                oneOfNextPlayersId = i;
                                lowestPower = players[i].getFiringPower();
                            }

                            i++;
                            if(i == 6){
                                i = 0;
                            }
                            if(i == goldOwnerIndex){
                                goldOwnerReached = true;
                            }
                        }

                        if (player.getFiringPower() > players[goldOwnerIndex].getFiringPower() && player.getPlayerId() != goldOwnerIndex && players[goldOwnerIndex].getProtection() == 0) {
                            if(goldOwnerIndex == 0){
                                Settings.shootChooseWindow(activityLayout, this, player);
                            }else {
                                handleShoot(player.getPlayerId(), goldOwnerIndex);
                                continueWithNextPlayer();
                            }
                        } else{
                            if(oneOfNextPlayersId != -1 && players[goldOwnerIndex].getFiringPower() > players[oneOfNextPlayersId].getFiringPower()) {
                                if (player.getFiringPower() > players[goldOwnerIndex].getFiringPower()) {
                                    int tmpFiringPower = players[oneOfNextPlayersId].getFiringPower();
                                    players[oneOfNextPlayersId].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                    players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                    boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][oneOfNextPlayersId];
                                    firingPowerKnowings[player.getPlayerId()][oneOfNextPlayersId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                    firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                    players[player.getPlayerId()].updateFiringPowerShowing();
                                    players[oneOfNextPlayersId].updateFiringPowerShowing();

                                    noteList.add(r.getString(R.string.swap__firing_power) + " " + players[oneOfNextPlayersId].getNameTV().getText());
                                    continueWithNextPlayer();
                                } else {
                                    int tmpFiringPower = players[goldOwnerIndex].getFiringPower();
                                    players[goldOwnerIndex].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                    players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                    boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][goldOwnerIndex];
                                    firingPowerKnowings[player.getPlayerId()][goldOwnerIndex] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                    firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                    players[player.getPlayerId()].updateFiringPowerShowing();
                                    players[goldOwnerIndex].updateFiringPowerShowing();

                                    noteList.add(r.getString(R.string.swap__firing_power) + " " + players[goldOwnerIndex].getNameTV().getText());
                                    continueWithNextPlayer();
                                }
                            }else {
                                int tmpFiringPower = players[goldOwnerIndex].getFiringPower();
                                players[goldOwnerIndex].setFiringPower(players[player.getPlayerId()].getFiringPower());
                                players[player.getPlayerId()].setFiringPower(tmpFiringPower);

                                boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][goldOwnerIndex];
                                firingPowerKnowings[player.getPlayerId()][goldOwnerIndex] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
                                firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
                                players[player.getPlayerId()].updateFiringPowerShowing();
                                players[goldOwnerIndex].updateFiringPowerShowing();

                                noteList.add(r.getString(R.string.swap__firing_power) + " " + players[goldOwnerIndex].getNameTV().getText());
                                continueWithNextPlayer();
                            }
                        }
                    } else {
                        player.setLives(player.getLives() + 1);
                        noteList.add(r.getString(R.string.increased_life));
                        continueWithNextPlayer();
                    }
                } else {
                    firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = true;
                    player.updateFiringPowerShowing();
                    noteList.add(r.getString(R.string.viewed_firing_power));
                    continueWithNextPlayer();
                }
            }
        }
    }

    private void randomAct(Player player, boolean goldInCoffin){
        List<String> candidates = new ArrayList<>();
        String SWAP = "swap";
        String SHOOT = "shoot";
        String VIEW = "view";
        String LIFE = "life";
        String GET_TOOL = "get_tool";
        String BUY_SELL = "buy_sell";
        String OPEN_COFFIN = "open_coffin";
        int swapPlayerId = 0;
        int shootPlayerId = 0;
        Resources r = getResources();

        if(player.getLives() < 10){
            candidates.add(LIFE);
        }

        if(firingPowerKnowings[player.getPlayerId()][player.getPlayerId()]){
            for(int i = 0; i < firingPowerKnowings.length; i++){
                if(!firingPowerKnowings[player.getPlayerId()][i] && player.getPlayerId() != players[i].getPlayerId()){
                    if(player.getFiringPower() < players[i].getFiringPower()){
                        candidates.add(SWAP);
                        swapPlayerId = players[i].getPlayerId();
                    }else if(players[i].getProtection() == 0){
                        candidates.add(SHOOT);
                        shootPlayerId = players[i].getPlayerId();
                    }
                }
            }
        }else{
            candidates.add(VIEW);

            List<Integer> ids = new ArrayList<>();
            for(int i = 0; i < 6; i++){
                if(i != player.getPlayerId() && players[i].getProtection() == 0){
                    ids.add(i);
                }
            }
            shootPlayerId = ids.get(new Random().nextInt(ids.size()));
            candidates.add(SHOOT);
        }

        if(goldInCoffin) {
            if(player.getSpades() > 0 && player.getWater() > 0 && player.getMap() > 0){
                candidates.add(OPEN_COFFIN);
            }
            if((player.getSpades() > 2 && (player.getWater() == 0 || player.getMap() == 0))
                    || (player.getMap() > 2 && (player.getSpades() == 0 || player.getWater() == 0))
                    || (player.getWater() > 2 && (player.getSpades() == 0 || player.getMap() == 0))
                    || (player.getSpades() > 1 && player.getWater() > 1 && player.getMap() == 0)
                    || (player.getMap() > 1 && player.getWater() > 1 && player.getSpades() == 0)
                    || (player.getMap() > 1 && player.getSpades() > 1 && player.getWater() == 0)){
                candidates.add(BUY_SELL);
            }
            candidates.add(GET_TOOL);
        }

        String selected = candidates.get(new Random().nextInt(candidates.size()));
        if(selected.equals(SWAP)){
            int tmpFiringPower = players[swapPlayerId].getFiringPower();
            players[swapPlayerId].setFiringPower(players[player.getPlayerId()].getFiringPower());
            players[player.getPlayerId()].setFiringPower(tmpFiringPower);

            boolean tmpFiringPowerKnowing = firingPowerKnowings[player.getPlayerId()][swapPlayerId];
            firingPowerKnowings[player.getPlayerId()][swapPlayerId] = firingPowerKnowings[player.getPlayerId()][player.getPlayerId()];
            firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = tmpFiringPowerKnowing;
            players[player.getPlayerId()].updateFiringPowerShowing();
            players[swapPlayerId].updateFiringPowerShowing();

            noteList.add(r.getString(R.string.swap__firing_power) + " " + players[swapPlayerId].getNameTV().getText());
            continueWithNextPlayer();
        }else if(selected.equals(SHOOT)){
            if(shootPlayerId == 0){
                Settings.shootChooseWindow(activityLayout, this, player);
                myService.stopRunnable();
            }else {
                handleShoot(player.getPlayerId(), shootPlayerId);
                continueWithNextPlayer();
            }
        }else if(selected.equals(VIEW)){
            firingPowerKnowings[player.getPlayerId()][player.getPlayerId()] = true;
            player.updateFiringPowerShowing();
            noteList.add(r.getString(R.string.viewed_firing_power));
            continueWithNextPlayer();
        }else if(selected.equals(LIFE)){
            player.setLives(player.getLives() + 1);
            noteList.add(r.getString(R.string.increased_life));
            continueWithNextPlayer();
        }else if(selected.equals(GET_TOOL)){
            switch(new Random().nextInt(3)){
                case 0:
                    player.setSpades(player.getSpades() + 1);
                    break;
                case 1:
                    player.setMap(player.getMap() + 1);
                    break;
                default:
                    player.setWater(player.getWater() + 1);
                    break;
            }
            noteList.add(r.getString(R.string.got_one_more) + " " + r.getString(R.string.tool));
            continueWithNextPlayer();
        }else if(selected.equals(BUY_SELL)){
            if(player.getSpades() > 1 && player.getWater() > 1){
                player.setSpades(player.getSpades() - 1);
                player.setWater(player.getWater() - 1);
                player.setMap(1);
                noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.spade) + ", " + r.getString(R.string.water) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                continueWithNextPlayer();
            }else if(player.getSpades() > 1 && player.getMap() > 1){
                player.setSpades(player.getSpades() - 1);
                player.setMap(player.getMap() - 1);
                player.setWater(1);
                noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.spade) + ", " + r.getString(R.string.map) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                continueWithNextPlayer();
            }else if(player.getWater() > 1 && player.getMap() > 1){
                player.setWater(player.getWater() - 1);
                player.setMap(player.getMap() - 1);
                player.setSpades(1);
                noteList.add(r.getString(R.string.sold) + ": " + r.getString(R.string.water) + ", " + r.getString(R.string.map) + ". " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                continueWithNextPlayer();
            }else if(player.getSpades() > 2){
                player.setSpades(player.getSpades() - 2);
                if(player.getMap() == 0){
                    player.setMap(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.spade) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                    continueWithNextPlayer();
                }else{
                    player.setWater(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.spade) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                    continueWithNextPlayer();
                }
            }else if(player.getWater() > 2){
                player.setWater(player.getWater() - 2);
                if(player.getMap() == 0){
                    player.setMap(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.water) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.map));
                    continueWithNextPlayer();
                }else{
                    player.setSpades(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.water) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                    continueWithNextPlayer();
                }
            }else if(player.getMap() > 2){
                player.setMap(player.getMap() - 2);
                if(player.getWater() == 0){
                    player.setWater(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.map) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.water));
                    continueWithNextPlayer();
                }else{
                    player.setSpades(1);
                    noteList.add(r.getString(R.string.sold) + ": 2 " + r.getString(R.string.map) + "s. " + r.getString(R.string.bought) + ": " + r.getString(R.string.spade));
                    continueWithNextPlayer();
                }
            }
        }else if(selected.equals(OPEN_COFFIN)){
            List<Integer> closedCoffinIds = new ArrayList<>();
            for (int i = 0; i < coffins.length; i++) {
                if (!coffins[i].isOpen()) {
                    closedCoffinIds.add(coffins[i].getCoffinId());
                }
            }

            Center.Coffin c = coffins[closedCoffinIds.get(new Random().nextInt(closedCoffinIds.size()))];
            c.setBackgroundResource(R.drawable.open_coffin);
            c.setOpen(true);

            String gold = r.getString(R.string.opened_a_coffin);

            if (c.isGold()) {
                player.setGold(true);
                goldOwnerIndex = player.getPlayerId();
                goldOwnerTurn = 0;
                gold = gold + " " + r.getString(R.string.and_found_the_gold);
            }

            player.setSpades(player.getSpades() - 1);
            player.setWater(player.getWater() - 1);
            player.setMap(player.getMap() - 1);
            noteList.add(gold);
            continueWithNextPlayer();
        }
    }

    public void handleOpponentHit(int shooterPlayerId, boolean decreaseLife){
        finalizeShoot(shooterPlayerId, 0, decreaseLife);
        continueWithNextPlayer();
        myService.setCallbacks(MainActivity.this);
        myService.resume();
    }

    private void updateUserNotes(){
        StringBuilder sb = new StringBuilder();
        int from;
        int to;

        if(noteList.size() < 3){
            from = 0;
            to = noteList.size();
        }else{
            from = noteList.size() - 3;
            to = noteList.size();
        }

        for (int i = from; i < to; i++) {
            sb.append(noteList.get(i) + "\n");
        }

        noteTV.setText(sb.toString());
    }

    private void updateOpponentNotes(){
        StringBuilder sb = new StringBuilder();
        int from;
        int to;

        if(noteList.size() < 4){
            from = 1;
            to = noteList.size() - 1;
        }else{
            from = noteList.size() - 4;
            to = noteList.size() - 1;
        }

        for (int i = from; i < to; i++) {
            sb.append(noteList.get(i) + "\n");
        }

        noteTV.setText(sb.toString());
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            MyService.LocalBinder binder = (MyService.LocalBinder) service;
            myService = binder.getService();
            bound = true;
            myService.setCallbacks(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            bound = false;
        }
    };

    @Override
    public void doSomething() {
        manageTurn();
    }

    public class Player extends LinearLayout {
        private int playerId;
        private TextView firingNumTV;
        private TextView livesNumTV;
        private TextView protectionNumTV;
        private LinearLayout firingIndicatorBarLL;
        private LinearLayout livesIndicatorBarLL;
        private LinearLayout armorLL;
        private LinearLayout goldLL;
        private int firingPower;
        private int lives;
        private int protection;
        private int spades;
        private int map;
        private int water;
        private boolean gold;
        private int layoutWidth;
        private int layoutHeight;
        private TextView nameTV;
        private TextView actTV;
        private LinearLayout actLayout;

        public Player(String name, int fPower, int lvs, int prot) {
            super(getApplicationContext());

            firingPower = fPower;
            lives = lvs;
            protection = prot;

            setOrientation(LinearLayout.VERTICAL);

            actLayout = new LinearLayout(getApplicationContext());
            actLayout.setOrientation(LinearLayout.HORIZONTAL);
            actLayout.setWeightSum(1f);
            LinearLayout.LayoutParams actParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            actParams.width = Settings.getRatedWidth(boardWidth, 410);
            actParams.height = Settings.getRatedHeight(boardHeight, 60);
            actLayout.setLayoutParams(actParams);
            actTV = new TextView(getApplicationContext());
            actTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            actTV.setWidth(Settings.getRatedWidth(boardWidth, 410));
            actLayout.addView(actTV);
            actLayout.setVisibility(View.VISIBLE);

            LinearLayout nameGoldProtectionLayout = new LinearLayout(getApplicationContext());
            nameGoldProtectionLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.width = Settings.getRatedWidth(boardWidth, 410);
            params.height = Settings.getRatedHeight(boardHeight, 60);
            nameGoldProtectionLayout.setLayoutParams(params);
            GradientDrawable border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            nameGoldProtectionLayout.setBackground(border);
            nameTV = new TextView(getApplicationContext());
            nameTV.setText(name);
            nameTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11);
            nameTV.setWidth(Settings.getRatedWidth(boardWidth, 290));
            nameGoldProtectionLayout.addView(nameTV);

            //gold frame
            LinearLayout goldFrameLL = new LinearLayout(getApplicationContext());
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight, 60);
            params.width = Settings.getRatedWidth(boardWidth, 35);
            goldFrameLL.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            goldFrameLL.setBackground(border);
            nameGoldProtectionLayout.addView(goldFrameLL);

            //gold
            goldLL = new LinearLayout(getApplicationContext());
            goldLL.setBackgroundColor(Color.TRANSPARENT);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight, 60);
            params.width = Settings.getRatedWidth(boardWidth, 35);
            goldLL.setLayoutParams(params);
            goldFrameLL.addView(goldLL);

            //armor frame
            LinearLayout armorFrameLL = new LinearLayout(getApplicationContext());
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight, 60);
            params.width = Settings.getRatedWidth(boardWidth, 35);
            armorFrameLL.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            armorFrameLL.setBackground(border);
            nameGoldProtectionLayout.addView(armorFrameLL);

            //armor
            armorLL = new LinearLayout(getApplicationContext());
            armorLL.setBackgroundColor(Color.TRANSPARENT);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight, 60);
            params.width = Settings.getRatedWidth(boardWidth, 35);
            armorLL.setLayoutParams(params);
            armorFrameLL.addView(armorLL);

            //protection number
            protectionNumTV = new TextView(getApplicationContext());
            protectionNumTV.setText(protection + "");
            protectionNumTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
            protectionNumTV.setWidth(Settings.getRatedWidth(boardWidth, 35));
            protectionNumTV.setGravity(Gravity.CENTER_HORIZONTAL);
            nameGoldProtectionLayout.addView(protectionNumTV);

            //firing power
            LinearLayout firingPowerLayout = new LinearLayout(getApplicationContext());
            firingPowerLayout.setOrientation(LinearLayout.HORIZONTAL);
            params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.height = Settings.getRatedHeight(boardHeight, 45);
            firingPowerLayout.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            firingPowerLayout.setBackground(border);
            TextView firingTV = new TextView(getApplicationContext());
            firingTV.setText("Firing Power:");
            firingTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            firingTV.setWidth(Settings.getRatedWidth(boardWidth, 180));
            firingPowerLayout.addView(firingTV);

            //firing indicator frame
            LinearLayout firingIndicatorFrameLL = new LinearLayout(getApplicationContext());
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight, 45);
            params.width = Settings.getRatedWidth(boardWidth,180);
            firingIndicatorFrameLL.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            firingIndicatorFrameLL.setBackground(border);
            firingPowerLayout.addView(firingIndicatorFrameLL);

            //firing indicator bar
            firingIndicatorBarLL = new LinearLayout(getApplicationContext());
            firingIndicatorBarLL.setBackgroundColor(Color.TRANSPARENT);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight,45);
            firingIndicatorBarLL.setLayoutParams(params);
            TextView tv = new TextView(getApplicationContext());
            tv.setText("Unknown");
            tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            firingIndicatorBarLL.addView(tv);
            firingIndicatorFrameLL.addView(firingIndicatorBarLL);

            //number
            firingNumTV = new TextView(getApplicationContext());
            firingNumTV.setText("?");
            firingNumTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            firingNumTV.setWidth(Settings.getRatedWidth(boardWidth,45));
            firingNumTV.setGravity(Gravity.CENTER_HORIZONTAL);
            firingPowerLayout.addView(firingNumTV);

            //lives
            LinearLayout livesLayout = new LinearLayout(getApplicationContext());
            livesLayout.setOrientation(LinearLayout.HORIZONTAL);
            livesLayout.setBackgroundColor(Color.RED);
            params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            params.height = Settings.getRatedHeight(boardHeight,45);
            livesLayout.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            livesLayout.setBackground(border);
            TextView livesTV = new TextView(getApplicationContext());
            livesTV.setText("Lives:");
            livesTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            livesTV.setWidth(Settings.getRatedWidth(boardWidth,180));
            livesLayout.addView(livesTV);

            //lives indicator frame
            LinearLayout livesIndicatorFrameLL = new LinearLayout(getApplicationContext());
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight,45);
            params.width = Settings.getRatedWidth(boardWidth,180);
            livesIndicatorFrameLL.setLayoutParams(params);
            border = new GradientDrawable();
            border.setColor(0xFFFFFFFF);
            border.setStroke(1, 0xFF000000);
            livesIndicatorFrameLL.setBackground(border);
            livesLayout.addView(livesIndicatorFrameLL);

            //lives indicator bar
            livesIndicatorBarLL = new LinearLayout(getApplicationContext());
            livesIndicatorBarLL.setBackgroundColor(Color.RED);
            params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight,45);
            params.width = Settings.getRatedWidth(boardWidth,lives * 18);
            livesIndicatorBarLL.setLayoutParams(params);
            livesIndicatorFrameLL.addView(livesIndicatorBarLL);

            //number
            livesNumTV = new TextView(getApplicationContext());
            livesNumTV.setText(lives + "");
            livesNumTV.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
            livesNumTV.setWidth(Settings.getRatedWidth(boardWidth,45));
            livesNumTV.setGravity(Gravity.CENTER_HORIZONTAL);
            livesLayout.addView(livesNumTV);

            addView(actLayout);
            addView(nameGoldProtectionLayout);
            addView(firingPowerLayout);
            addView(livesLayout);

            setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!(v instanceof Player)) {
                        return;
                    }

                    Player p = (Player) v;
                    if (swapFiringPowerAction && p.getPlayerId() != 0) {
                        handleFiringPowerSwap(playerId);
                    } else if (shootAction && p.getPlayerId() != 0 && p.getProtection() == 0) {
                        handleShoot(0, playerId);
                    }
                }
            });

            layoutHeight = Settings.getRatedHeight(boardHeight, 210);
            layoutWidth = Settings.getRatedWidth(boardWidth, 400);
        }

        public void updateFiringPowerShowing(){
            LinearLayout.LayoutParams params;

            if(firingPowerKnowings[0][playerId]){
                firingIndicatorBarLL.setBackgroundColor(Color.YELLOW);
                params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                params.height = Settings.getRatedHeight(boardWidth,45);
                params.width = Settings.getRatedWidth(boardWidth,firingPower * 18);
                firingIndicatorBarLL.removeAllViews();
                firingIndicatorBarLL.setLayoutParams(params);

                firingNumTV.setText(firingPower + "");
            }else{
                firingIndicatorBarLL.setBackgroundColor(Color.WHITE);
                params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                params.height = Settings.getRatedHeight(boardHeight,45);
                TextView tv = new TextView(getApplicationContext());
                tv.setText("Unknown");
                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 8);
                firingIndicatorBarLL.removeAllViews();
                firingIndicatorBarLL.addView(tv);
                firingIndicatorBarLL.setLayoutParams(params);

                firingNumTV.setText("?");
            }
        }

        public void showLastAct(){
            actTV.setText(noteList.get(noteList.size() - 1));
            actLayout.setVisibility(View.VISIBLE);
        }

        public void hideLastAct(){
            actLayout.setVisibility(View.INVISIBLE);
        }

        public TextView getNameTV() {
            return nameTV;
        }

        public void setNameTV(TextView nameTV) {
            this.nameTV = nameTV;
        }

        public int getPlayerId() {
            return playerId;
        }

        public void setPlayerId(int playerId) {
            this.playerId = playerId;
        }

        public int getLives() {
            return lives;
        }

        public void setLives(int lives) {
            if(lives > 10){
                lives = 10;
            }
            this.lives = lives;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.height = Settings.getRatedHeight(boardHeight,45);
            params.width = Settings.getRatedWidth(boardWidth,this.lives * 18);
            livesIndicatorBarLL.setLayoutParams(params);
            livesNumTV.setText(this.lives + "");

            if(this.lives == 0){
                setProtection(2);
            }
        }

        public int getFiringPower() {
            return firingPower;
        }

        public void setFiringPower(int firingPower) {
            this.firingPower = firingPower;
        }

        public int getProtection() {
            return protection;
        }

        public void setProtection(int protection) {
            this.protection = protection;

            protectionNumTV.setText(this.protection + "");
            switch(this.protection){
                case 2:
                    armorLL.setBackgroundColor(Color.DKGRAY);
                    break;
                case 1:
                    armorLL.setBackgroundColor(Color.LTGRAY);
                    break;
                default:
                    armorLL.setBackgroundColor(Color.TRANSPARENT);
                    break;
            }
        }

        public int getSpades() {
            return spades;
        }

        public void setSpades(int spades) {
            this.spades = spades;
        }

        public int getMap() {
            return map;
        }

        public void setMap(int map) {
            this.map = map;
        }

        public int getWater() {
            return water;
        }

        public void setWater(int water) {
            this.water = water;
        }

        public boolean isGold() {
            return gold;
        }

        public void setGold(boolean gold) {
            this.gold = gold;

            if (this.gold) {
                goldLL.setBackgroundColor(Color.YELLOW);
            } else {
                goldLL.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        public int getLayoutWidth() {
            return layoutWidth;
        }

        public void setLayoutWidth(int layoutWidth) {
            this.layoutWidth = layoutWidth;
        }

        public int getLayoutHeight() {
            return layoutHeight;
        }

        public void setLayoutHeight(int layoutHeight) {
            this.layoutHeight = layoutHeight;
        }
    }

    class Center extends LinearLayout{
        private AppCompatActivity activity;
        private int layoutWidth;
        private int layoutHeight;

        public Center(AppCompatActivity act){
            super(getApplicationContext());
            activity = act;
            setOrientation(LinearLayout.HORIZONTAL);

            Coffin coffin = new Coffin(activity);
            coffin.setCoffinId(0);
            addView(coffin);
            coffins[coffin.getCoffinId()] = coffin;

            coffin = new Coffin(activity);
            coffin.setCoffinId(1);
            addView(coffin);
            coffins[coffin.getCoffinId()] = coffin;

            coffin = new Coffin(activity);
            coffin.setCoffinId(2);
            addView(coffin);
            coffins[coffin.getCoffinId()] = coffin;

            coffin = new Coffin(activity);
            coffin.setCoffinId(3);
            addView(coffin);
            coffins[coffin.getCoffinId()] = coffin;

            coffin = new Coffin(activity);
            coffin.setCoffinId(4);
            addView(coffin);
            coffins[coffin.getCoffinId()] = coffin;

            coffins[new Random().nextInt(5)].setGold(true);

            layoutHeight = Settings.getRatedHeight(boardHeight, 130);
            layoutWidth = Settings.getRatedWidth(boardWidth, 500);
        }

        public int getLayoutWidth() {
            return layoutWidth;
        }

        public void setLayoutWidth(int layoutWidth) {
            this.layoutWidth = layoutWidth;
        }

        public int getLayoutHeight() {
            return layoutHeight;
        }

        public void setLayoutHeight(int layoutHeight) {
            this.layoutHeight = layoutHeight;
        }

        class Coffin extends androidx.appcompat.widget.AppCompatImageView {
            private AppCompatActivity activity;
            private int coffinId;
            private boolean gold;
            private boolean open;

            public Coffin(AppCompatActivity act){
                super(getApplicationContext());
                activity = act;
                open = false;

                LayoutParams params = new LayoutParams(Settings.getRatedWidth(boardWidth,90), Settings.getRatedHeight(boardHeight,130));
                params.setMargins(Settings.getRatedWidth(boardWidth,5), 0, Settings.getRatedHeight(boardHeight,5), 0);
                setLayoutParams(params);

                setBackgroundResource(R.drawable.closed_coffin);

                setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(!(v instanceof Coffin)) {
                            return;
                        }

                        Coffin c = (Coffin) v;
                        if (openCoffinAction && !c.isOpen()) {
                            handleCoffinOpening(coffinId);
                        }
                    }
                });
            }

            public int getCoffinId() {
                return coffinId;
            }

            public void setCoffinId(int coffinId) {
                this.coffinId = coffinId;
            }

            public boolean isGold() {
                return gold;
            }

            public void setGold(boolean gold) {
                this.gold = gold;
            }

            public boolean isOpen() {
                return open;
            }

            public void setOpen(boolean open) {
                this.open = open;
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        unbindService();
    }
}
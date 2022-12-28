package com.ak17apps.ecstasyofgold.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.ak17apps.ecstasyofgold.R;
import com.ak17apps.ecstasyofgold.layouts.MainActivity;

import java.util.List;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Settings {
    private static int spade;
    private static int map;
    private static int water;
    private static int nrOfSelectedItemsToSell;
    private static int nrOfSelectedItemsToBuy;

    public static int getRatedHeight(int screenHeight, int height){
        return height * screenHeight / 1080;
    }

    public static int getRatedWidth(int screenWidth, int width){
        return width * screenWidth / 1920;
    }

    public static void shootChooseWindow(final View view, final AppCompatActivity activity, final MainActivity.Player shooterPlayer){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.window_shoot_choose, null);

        int width = (int) (view.getWidth() * 0.7);
        int height = (int) (view.getHeight() * 0.7);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView opponentNameTV = popupView.findViewById(R.id.opponentNameTV);
        opponentNameTV.setText(shooterPlayer.getNameTV().getText() + " " + activity.getResources().getString(R.string.shoots_at_you));

        TextView hitTV = popupView.findViewById(R.id.hitTV);
        hitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)activity).handleOpponentHit(shooterPlayer.getPlayerId(), true);
                popupWindow.dismiss();
            }
        });

        TextView notHitTV = popupView.findViewById(R.id.notHitTV);
        notHitTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)activity).handleOpponentHit(shooterPlayer.getPlayerId(), false);
                popupWindow.dismiss();
            }
        });
    }

    public static void inventoryWindow(final View view, final AppCompatActivity activity, final Context con, final MainActivity.Player player){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.window_inventory, null);

        int width = (int) (view.getWidth() * 0.7);
        int height = (int) (view.getHeight() * 0.7);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView spadeInvTV = popupView.findViewById(R.id.spadeInvTV);
        spadeInvTV.setText(con.getResources().getString(R.string.spade) + ": " + player.getSpades());
        TextView mapInvTV = popupView.findViewById(R.id.mapInvTV);
        mapInvTV.setText(con.getResources().getString(R.string.map) + ": " + player.getMap());
        TextView waterInvTV = popupView.findViewById(R.id.waterInvTV);
        waterInvTV.setText(con.getResources().getString(R.string.water) + ": " + player.getWater());

        TextView okTV = popupView.findViewById(R.id.okTV);
        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public static void errorWindow(final View view, final AppCompatActivity activity, String str){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.window_error, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, view.getWidth(), view.getHeight(), true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView tv = popupView.findViewById(R.id.tv);
        tv.setText(str);
    }

    public static void fullScreenHistoryWindow(final View view, final AppCompatActivity activity, List<String> noteList){
        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.window_full_screen_history, null);

        final PopupWindow popupWindow = new PopupWindow(popupView, (int)(view.getWidth() * 0.9), (int)(view.getHeight() * 0.9), true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView tv = popupView.findViewById(R.id.tv);
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < noteList.size(); i++){
            sb.append(noteList.get(i) + " ");
            if(i % 2 == 1){
                sb.append("\n");
            }
        }

        tv.setText(sb.toString());
        final ScrollView sc = popupView.findViewById(R.id.sc);
        sc.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                sc.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        TextView closeTV = popupView.findViewById(R.id.closeTV);
        closeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }

    public static void buySellWindow(final View view, final AppCompatActivity activity, final Context con, final MainActivity.Player player) {
        TextView spadeInvTV;
        TextView mapInvTV;
        TextView waterInvTV;
        final ImageView spadeSell1IV;
        final ImageView spadeSell2IV;
        final ImageView mapSell1IV;
        final ImageView mapSell2IV;
        final ImageView waterSell1IV;
        final ImageView waterSell2IV;
        final ImageView spadeBuyIV;
        final ImageView mapBuyIV;
        final ImageView waterBuyIV;

        TextView cancelTV;
        TextView okTV;

        spade = player.getSpades();
        map = player.getMap();
        water = player.getWater();
        nrOfSelectedItemsToSell = 0;
        nrOfSelectedItemsToBuy = 0;

        LayoutInflater inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") View popupView = inflater.inflate(R.layout.window_buy_sell, null);

        int width = (int) (view.getWidth() * 0.9);
        int height = (int) (view.getHeight() * 0.9);

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, true);
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        spadeInvTV = popupView.findViewById(R.id.spadeInvTV);
        spadeInvTV.setText(con.getResources().getString(R.string.spade) + ": " + player.getSpades());
        mapInvTV = popupView.findViewById(R.id.mapInvTV);
        mapInvTV.setText(con.getResources().getString(R.string.map) + ": " + player.getMap());
        waterInvTV = popupView.findViewById(R.id.waterInvTV);
        waterInvTV.setText(con.getResources().getString(R.string.water) + ": " + player.getWater());

        spadeSell1IV = popupView.findViewById(R.id.spadeSell1IV);
        spadeSell1IV.setBackground(con.getResources().getDrawable(R.drawable.spade));
        spadeSell1IV.setSelected(false);
        spadeSell1IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spadeSell1IV.isSelected()) {
                    if(spade > 0 && nrOfSelectedItemsToSell < 2) {
                        spadeSell1IV.setBackground(con.getResources().getDrawable(R.drawable.spade_selected));
                        spadeSell1IV.setSelected(true);
                        spade--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(spade < player.getSpades()) {
                        spadeSell1IV.setBackground(con.getResources().getDrawable(R.drawable.spade));
                        spadeSell1IV.setSelected(false);
                        spade++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        spadeSell2IV = popupView.findViewById(R.id.spadeSell2IV);
        spadeSell2IV.setBackground(con.getResources().getDrawable(R.drawable.spade));
        spadeSell2IV.setSelected(false);
        spadeSell2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!spadeSell2IV.isSelected()){
                    if(spade > 0 && nrOfSelectedItemsToSell < 2) {
                        spadeSell2IV.setBackground(con.getResources().getDrawable(R.drawable.spade_selected));
                        spadeSell2IV.setSelected(true);
                        spade--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(spade < player.getSpades()) {
                        spadeSell2IV.setBackground(con.getResources().getDrawable(R.drawable.spade));
                        spadeSell2IV.setSelected(false);
                        spade++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        mapSell1IV = popupView.findViewById(R.id.mapSell1IV);
        mapSell1IV.setBackground(con.getResources().getDrawable(R.drawable.map));
        mapSell1IV.setSelected(false);
        mapSell1IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mapSell1IV.isSelected()){
                    if(map > 0 && nrOfSelectedItemsToSell < 2) {
                        mapSell1IV.setBackground(con.getResources().getDrawable(R.drawable.map_selected));
                        mapSell1IV.setSelected(true);
                        map--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(map < player.getMap()) {
                        mapSell1IV.setBackground(con.getResources().getDrawable(R.drawable.map));
                        mapSell1IV.setSelected(false);
                        map++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        mapSell2IV = popupView.findViewById(R.id.mapSell2IV);
        mapSell2IV.setBackground(con.getResources().getDrawable(R.drawable.map));
        mapSell2IV.setSelected(false);
        mapSell2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mapSell2IV.isSelected()){
                    if(map > 0 && nrOfSelectedItemsToSell < 2) {
                        mapSell2IV.setBackground(con.getResources().getDrawable(R.drawable.map_selected));
                        mapSell2IV.setSelected(true);
                        map--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(map < player.getMap()) {
                        mapSell2IV.setBackground(con.getResources().getDrawable(R.drawable.map));
                        mapSell2IV.setSelected(false);
                        map++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        waterSell1IV = popupView.findViewById(R.id.waterSell1IV);
        waterSell1IV.setBackground(con.getResources().getDrawable(R.drawable.water));
        waterSell1IV.setSelected(false);
        waterSell1IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!waterSell1IV.isSelected()){
                    if(water > 0 && nrOfSelectedItemsToSell < 2) {
                        waterSell1IV.setBackground(con.getResources().getDrawable(R.drawable.water_selected));
                        waterSell1IV.setSelected(true);
                        water--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(water < player.getWater()) {
                        waterSell1IV.setBackground(con.getResources().getDrawable(R.drawable.water));
                        waterSell1IV.setSelected(false);
                        water++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        waterSell2IV = popupView.findViewById(R.id.waterSell2IV);
        waterSell2IV.setBackground(con.getResources().getDrawable(R.drawable.water));
        waterSell2IV.setSelected(false);
        waterSell2IV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!waterSell2IV.isSelected()){
                    if(water > 0 && nrOfSelectedItemsToSell < 2) {
                        waterSell2IV.setBackground(con.getResources().getDrawable(R.drawable.water_selected));
                        waterSell2IV.setSelected(true);
                        water--;
                        nrOfSelectedItemsToSell++;
                    }
                }else{
                    if(water < player.getWater()) {
                        waterSell2IV.setBackground(con.getResources().getDrawable(R.drawable.water));
                        waterSell2IV.setSelected(false);
                        water++;
                        nrOfSelectedItemsToSell--;
                    }
                }
            }
        });

        spadeBuyIV = popupView.findViewById(R.id.spadeBuyIV);
        spadeBuyIV.setBackground(con.getResources().getDrawable(R.drawable.spade));
        spadeBuyIV.setSelected(false);
        spadeBuyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(spadeBuyIV.isSelected()){
                    spadeBuyIV.setBackground(con.getResources().getDrawable(R.drawable.spade));
                    spadeBuyIV.setSelected(false);
                    spade--;
                    nrOfSelectedItemsToBuy--;
                }else{
                    if(nrOfSelectedItemsToBuy < 1) {
                        spadeBuyIV.setBackground(con.getResources().getDrawable(R.drawable.spade_selected));
                        spadeBuyIV.setSelected(true);
                        spade++;
                        nrOfSelectedItemsToBuy++;
                    }
                }
            }
        });

        mapBuyIV = popupView.findViewById(R.id.mapBuyIV);
        mapBuyIV.setBackground(con.getResources().getDrawable(R.drawable.map));
        mapBuyIV.setSelected(false);
        mapBuyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mapBuyIV.isSelected()){
                    mapBuyIV.setBackground(con.getResources().getDrawable(R.drawable.map));
                    mapBuyIV.setSelected(false);
                    map--;
                    nrOfSelectedItemsToBuy--;
                }else{
                    if(nrOfSelectedItemsToBuy < 1) {
                        mapBuyIV.setBackground(con.getResources().getDrawable(R.drawable.map_selected));
                        mapBuyIV.setSelected(true);
                        map++;
                        nrOfSelectedItemsToBuy++;
                    }
                }
            }
        });

        waterBuyIV = popupView.findViewById(R.id.waterBuyIV);
        waterBuyIV.setBackground(con.getResources().getDrawable(R.drawable.water));
        waterBuyIV.setSelected(false);
        waterBuyIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(waterBuyIV.isSelected()){
                    waterBuyIV.setBackground(con.getResources().getDrawable(R.drawable.water));
                    waterBuyIV.setSelected(false);
                    water--;
                    nrOfSelectedItemsToBuy--;
                }else{
                    if(nrOfSelectedItemsToBuy < 1) {
                        waterBuyIV.setBackground(con.getResources().getDrawable(R.drawable.water_selected));
                        waterBuyIV.setSelected(true);
                        water++;
                        nrOfSelectedItemsToBuy++;
                    }
                }
            }
        });

        cancelTV = popupView.findViewById(R.id.cancelTV);
        cancelTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        okTV = popupView.findViewById(R.id.okTV);
        okTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                player.setSpades(spade);
                player.setMap(map);
                player.setWater(water);
                ((MainActivity)activity).buySellTool(player);
                popupWindow.dismiss();
            }
        });
    }
}

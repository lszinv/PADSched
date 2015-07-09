package com.lsz.padsched;

import com.lsz.padsched.R;

public class EventInfoObject {
    private int eventName;
    private int eventDrawable;

    public EventInfoObject(int event) {
        switch (event) {

            case Constants.EMERALD_DORA:
                setEventDrawable(R.drawable.emerald);
                setEventName(R.string.eme);
                break;

            case Constants.SAPPHIRE_DORA:
                setEventDrawable(R.drawable.sapphire);
                setEventName(R.string.safa);
                break;

            case Constants.RUBY_DORA:
                setEventDrawable(R.drawable.ruby);
                setEventName(R.string.ruby);
                break;

            case Constants.META_DORA:
                setEventDrawable(R.drawable.metal);
                setEventName(R.string.meta);
                break;

            case Constants.GOLD_DORA:
                setEventDrawable(R.drawable.gold);
                setEventName(R.string.gold);
                break;

            case Constants.META_GOLD:
                setEventDrawable(R.drawable.meta_gold);
                setEventName(R.string.metagold);
                break;

            case Constants.PENGU_DORA:
                setEventDrawable(R.drawable.pengdra);
                setEventName(R.string.pengu);
                break;

            case Constants.KING_CARNIVAL:
                setEventDrawable(R.drawable.king_carnival);
                setEventName(R.string.kingCarnival);
                break;

            case Constants.SUPER_KING:
                setEventDrawable(R.drawable.super_king);
                setEventName(R.string.superKingCarnival);
                break;

            case Constants.EVO_RUSH:
                setEventDrawable(R.drawable.evolution);
                setEventName(R.string.evo);
                break;

            case Constants.STAR_VAULT:
                setEventDrawable(R.drawable.starvault);
                setEventName(R.string.starvault);
                break;

            case Constants.DRAGON_PLANTS:
                setEventDrawable(R.drawable.dragon_plant);
                setEventName(R.string.dragonPlants);
                break;

            case Constants.EXTREME_METAL:
                setEventDrawable(R.drawable.extreme);
                setEventName(R.string.extreme);
                break;

            case Constants.TAMADRA:
                setEventDrawable(R.drawable.tamadra);
                setEventName(R.string.tama);
                break;

            case Constants.COIN:
                setEventDrawable(R.drawable.coin);
                setEventName(R.string.coin);
                break;


            default:
                setEventDrawable(R.drawable.unknown);
                setEventName(R.string.unknown);
                break;
        }
    }

    public int getEventName() {
        return eventName;
    }

    public void setEventName(int eventName) {
        this.eventName = eventName;
    }

    public int getEventDrawable() {
        return eventDrawable;
    }

    public void setEventDrawable(int eventDrawable) {
        this.eventDrawable = eventDrawable;
    }

    public int getNotification() {
        return R.drawable.notification;
    }

}

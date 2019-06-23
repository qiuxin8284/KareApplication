package com.kaer.more.manager;

import android.content.Context;

import scifly.device.Device;

public final class KaerManager {

    /**
     * 梯形调整
     */
    public static final class TrapezoidalAdjustment {

        public static boolean getAutoKeyStone() {
            return Device.getAutoKeyStone();
        }

        public static void setAutoKeyStone(boolean is_auto) {
            Device.setAutoKeyStone(is_auto);
        }

        /**
         *
         * @param is_vertical 转向：true 垂直，false 水平
         * @param value ：取值范围-50~50
         */
        public static void setManualKeyStone(boolean is_vertical, int value){
            if (value >= -50 && value <= 50) {
                if (is_vertical) {
                    Device.setManualVerticalKeyStone(value);
                } else {
                    Device.setManualHorizontalKeyStone(value);
                }
            } else {
                throw new RuntimeException("ManualKeyStone value false, from -50 to 50");
            }
        }

        public static int getManualKeyStone(boolean is_vertical) {
            if (is_vertical){
                return Device.getManualVerticalKeyStone();
            } else {
                return Device.getManualHorizontalKeyStone();
            }
        }
    }

    public static final class DirectionAdjustment{
        public static boolean getAutoKeyStone(Context ctx) {
            return Device.getAutoProjector(ctx);
        }

        public static void setAutoKeyStone(Context ctx, boolean is_auto) {
            Device.setAutoProject(ctx, is_auto);
        }

        public static int getDirection(Context ctx){
            return Device.getProjectorDirect(ctx);
        }

        public static void setDirection(Context ctx, int value) {
            if (value != Device.PROJECTOR_DIRECTION_FRONT && value != Device.PROJECTOR_DIRECTION_HORIZONTAL &&
                    value != Device.PROJECTOR_DIRECTION_VERTICAL && value != Device.PROJECTOR_DIRECTION_VERTICAL_HORIZONTAL) {
                throw new RuntimeException("Direction value false！！！");
            } else {
                Device.setProjectorDirect(ctx, value);
            }
        }
    }

    public static final class OtherUtils{
        public static final boolean getPower(){
            return Device.getProjectorLedPower();
        }

        public static final void setPower(boolean turn_on) {
            if (turn_on) {
                Device.setProjectorLedPower(Device.EOS_PROJECTOR_LED_POWER_ON);
            } else {
                Device.setProjectorLedPower(Device.EOS_PROJECTOR_LED_POWER_OFF);
            }
        }
    }
}

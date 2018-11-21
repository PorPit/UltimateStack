package com.porpit.ultimatestack.transform.patch;

import net.minecraft.client.gui.FontRenderer;

import java.util.HashMap;
import java.util.Map;

public class RenderItemPatch {

    public final static Map<Character,Character> digit = new HashMap<>();
    static{
        digit.put('0', '₀');
        digit.put('1', '₁');
        digit.put('2', '₂');
        digit.put('3', '₃');
        digit.put('4', '₄');
        digit.put('5', '₅');
        digit.put('6', '₆');
        digit.put('7', '₇');
        digit.put('8', '₈');
        digit.put('9', '₉');
    }
    public static int drawItemCountWithShadow(FontRenderer fr, String text, float x, float y, int color) {

        String textModify=text;
        char[] textChars=textModify.toCharArray();
        if(textChars.length>3){
            for (int i=0;i<textChars.length;i++){
                if(Character.isDigit(textChars[i])){
                    textChars[i]=digit.get(textChars[i]);
                }
            }
            textModify=new String(textChars);
            return  fr.drawStringWithShadow(textModify,x+(float) (fr.getStringWidth("₀")*1.3 ), y-2, color);

        }
        return  fr.drawStringWithShadow(textModify,x , y, color);

    }
}

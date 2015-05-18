/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.avoscloud.chat.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.text.Spannable;
import android.text.Spannable.Factory;
import android.text.style.ImageSpan;

import com.beabox.hjy.tt.R;
/**
 * @author zhupei
 *
 */
public class SmileUtils {
	public static final String emoji_1 = "[可爱]" ;
	public static final String emoji_2 = "[笑脸]" ;
	public static final String emoji_3 = "[囧]" ;
	public static final String emoji_4 = "[生气]" ;
	public static final String emoji_5 = "[鬼脸]" ;
	public static final String emoji_6 = "[花心]" ;
	public static final String emoji_7 = "[害怕]" ;
	public static final String emoji_8 = "[我汗]" ;
	public static final String emoji_9 = "[尴尬]" ;
	public static final String emoji_10 = "[哼哼]" ;
	public static final String emoji_11 = "[忧郁]" ;
	public static final String emoji_12 = "[呲牙]" ;
	public static final String emoji_13 = "[媚眼]" ;
	public static final String emoji_14 = "[累]" ;
	public static final String emoji_15 = "[苦逼]" ;
	public static final String emoji_16 = "[瞌睡]" ;
	public static final String emoji_17 = "[哎呀]" ;
	public static final String emoji_18 = "[刺瞎]" ;
	public static final String emoji_19 = "[哭]" ;
	public static final String emoji_20 = "[激动]" ;
	public static final String emoji_21 = "[难过]" ;
	public static final String emoji_22 = "[害羞]" ;
	public static final String emoji_23 = "[高兴]" ;
	public static final String emoji_24 = "[愤怒]" ;
	public static final String emoji_25 = "[亲]" ;
	public static final String emoji_26 = "[飞吻]" ;
	public static final String emoji_27 = "[得意]" ;
	public static final String emoji_28 = "[惊恐]" ;
	public static final String emoji_29 = "[口罩]" ;
	public static final String emoji_30 = "[惊讶]" ;
	public static final String emoji_31 = "[委屈]" ;
	public static final String emoji_32 = "[生病]" ;
	public static final String emoji_33 = "[红心]" ;
	public static final String emoji_34 = "[心碎]" ;
	public static final String emoji_35 = "[玫瑰]" ;
	public static final String emoji_36 = "[花]" ;
	public static final String emoji_37 = "[外星人]" ;
	public static final String emoji_38 = "[金牛座]" ;
	public static final String emoji_39 = "[双子座]" ;
	public static final String emoji_40 = "[巨蟹座]" ;
	public static final String emoji_41 = "[狮子座]" ;
	public static final String emoji_42 = "[处女座]" ;
	public static final String emoji_43 = "[天平座]" ;
	public static final String emoji_44 = "[天蝎座]" ;
	public static final String emoji_45 = "[射手座]" ;
	public static final String emoji_46 = "[摩羯座]" ;
	public static final String emoji_47 = "[水瓶座]" ;
	public static final String emoji_48 = "[白羊座]" ;
	public static final String emoji_49 = "[双鱼座]" ;
	public static final String emoji_50 = "[星座]" ;
	public static final String emoji_51 = "[男孩]" ;
	public static final String emoji_52 = "[女孩]" ;
	public static final String emoji_53 = "[嘴唇]" ;
	public static final String emoji_54 = "[爸爸]" ;
	public static final String emoji_55 = "[妈妈]" ;
	public static final String emoji_56 = "[衣服]" ;
	public static final String emoji_57 = "[皮鞋]" ;
	public static final String emoji_58 = "[照相]" ;
	public static final String emoji_59 = "[电话]" ;
	public static final String emoji_60 = "[石头]" ;
	public static final String emoji_61 = "[胜利]" ;
	public static final String emoji_62 = "[禁止]" ;
	public static final String emoji_63 = "[滑雪]" ;
	public static final String emoji_64 = "[高尔夫]" ;
	public static final String emoji_65 = "[网球]" ;
	public static final String emoji_66 = "[棒球]" ;
	public static final String emoji_67 = "[冲浪]" ;
	public static final String emoji_68 = "[足球]" ;
	public static final String emoji_69 = "[小鱼]" ;
	public static final String emoji_70 = "[问号]" ;
	public static final String emoji_71 = "[叹号]" ;
	public static final String emoji_72 = "[顶]" ;
	public static final String emoji_73 = "[写字]" ;
	public static final String emoji_74 = "[衬衫]" ;
	public static final String emoji_75 = "[小花]" ;
	public static final String emoji_76 = "[郁金香]" ;
	public static final String emoji_77 = "[向日葵]" ;
	public static final String emoji_78 = "[鲜花]" ;
	public static final String emoji_79 = "[椰树]" ;
	public static final String emoji_80 = "[仙人掌]" ;
	public static final String emoji_81 = "[气球]" ;
	public static final String emoji_82 = "[hong]" ;
	public static final String emoji_83 = "[喝彩]" ;
	public static final String emoji_84 = "[剪子]" ;
	public static final String emoji_85 = "[蝴蝶结]" ;
	public static final String emoji_86 = "[机密]" ;
	public static final String emoji_87 = "[铃声]" ;
	public static final String emoji_88 = "[女帽]" ;
	public static final String emoji_89 = "[裙子]" ;
	public static final String emoji_90 = "[理发店]" ;
	public static final String emoji_91 = "[和服]" ;
	public static final String emoji_92 = "[比基尼]" ;
	public static final String emoji_93 = "[拎包]" ;
	public static final String emoji_94 = "[拍摄]" ;
	public static final String emoji_95 = "[铃铛]" ;
	public static final String emoji_96 = "[音乐]" ;
	public static final String emoji_97 = "[心星]" ;
	public static final String emoji_98 = "[粉心]" ;
	public static final String emoji_99 = "[丘比特]" ;
	public static final String emoji_100 = "[吹气]" ;
	public static final String emoji_101 = "[口水]" ;
	public static final String emoji_102 = "[对]" ;
	public static final String emoji_103 = "[错]" ;
	public static final String emoji_104 = "[绿茶]" ;
	public static final String emoji_105 = "[面包]" ;
	public static final String emoji_106 = "[面条]" ;
	public static final String emoji_107 = "[咖喱饭]" ;
	public static final String emoji_108 = "[饭团]" ;
	public static final String emoji_109 = "[麻辣烫]" ;
	public static final String emoji_110 = "[寿司]" ;
	public static final String emoji_111 = "[苹果]" ;
	public static final String emoji_112 = "[橙子]" ;
	public static final String emoji_113 = "[草莓]" ;
	public static final String emoji_114 = "[西瓜]" ;
	public static final String emoji_115 = "[柿子]" ;
	public static final String emoji_116 = "[眼睛]" ;
	public static final String emoji_117 = "[好的]" ;
	
	private static final Factory spannableFactory = Spannable.Factory
	        .getInstance();
	
	private static final Map<Pattern, Integer> emoticons = new HashMap<Pattern, Integer>();

	static {
	
	    addPattern(emoticons, emoji_1, R.drawable.emoji_1);
	    addPattern(emoticons, emoji_2, R.drawable.emoji_2);
	    addPattern(emoticons, emoji_3, R.drawable.emoji_3);
	    addPattern(emoticons, emoji_4, R.drawable.emoji_4);
	    addPattern(emoticons, emoji_5, R.drawable.emoji_5);
	    addPattern(emoticons, emoji_6, R.drawable.emoji_6);
	    addPattern(emoticons, emoji_7, R.drawable.emoji_7);
	    addPattern(emoticons, emoji_8, R.drawable.emoji_8);
	    addPattern(emoticons, emoji_9, R.drawable.emoji_9);
	    addPattern(emoticons, emoji_10, R.drawable.emoji_10);
	    addPattern(emoticons, emoji_11, R.drawable.emoji_11);
	    addPattern(emoticons, emoji_12, R.drawable.emoji_12);
	    addPattern(emoticons, emoji_13, R.drawable.emoji_13);
	    addPattern(emoticons, emoji_14, R.drawable.emoji_14);
	    addPattern(emoticons, emoji_15, R.drawable.emoji_15);
	    addPattern(emoticons, emoji_16, R.drawable.emoji_16);
	    addPattern(emoticons, emoji_17, R.drawable.emoji_17);
	    addPattern(emoticons, emoji_18, R.drawable.emoji_18);
	    addPattern(emoticons, emoji_19, R.drawable.emoji_19);
	    addPattern(emoticons, emoji_20, R.drawable.emoji_20);
	    addPattern(emoticons, emoji_21, R.drawable.emoji_21);
	    addPattern(emoticons, emoji_22, R.drawable.emoji_22);
	    addPattern(emoticons, emoji_23, R.drawable.emoji_23);
	    addPattern(emoticons, emoji_24, R.drawable.emoji_24);
	    addPattern(emoticons, emoji_25, R.drawable.emoji_25);
	    addPattern(emoticons, emoji_26, R.drawable.emoji_26);
	    addPattern(emoticons, emoji_27, R.drawable.emoji_27);
	    addPattern(emoticons, emoji_28, R.drawable.emoji_28);
	    addPattern(emoticons, emoji_29, R.drawable.emoji_29);
	    addPattern(emoticons, emoji_30, R.drawable.emoji_30);
	    addPattern(emoticons, emoji_31, R.drawable.emoji_31);
	    addPattern(emoticons, emoji_32, R.drawable.emoji_32);
	    addPattern(emoticons, emoji_33, R.drawable.emoji_33);
	    addPattern(emoticons, emoji_34, R.drawable.emoji_34);
	    addPattern(emoticons, emoji_35, R.drawable.emoji_35);
	    addPattern(emoticons, emoji_36, R.drawable.emoji_36);
	    addPattern(emoticons, emoji_37, R.drawable.emoji_37);
	    addPattern(emoticons, emoji_38, R.drawable.emoji_38);
	    addPattern(emoticons, emoji_39, R.drawable.emoji_39);
	    addPattern(emoticons, emoji_40, R.drawable.emoji_40);
	    addPattern(emoticons, emoji_41, R.drawable.emoji_41);
	    addPattern(emoticons, emoji_42, R.drawable.emoji_42);
	    addPattern(emoticons, emoji_43, R.drawable.emoji_43);
	    addPattern(emoticons, emoji_44, R.drawable.emoji_44);
	    addPattern(emoticons, emoji_45, R.drawable.emoji_45);
	    addPattern(emoticons, emoji_46, R.drawable.emoji_46);
	    addPattern(emoticons, emoji_47, R.drawable.emoji_47);
	    addPattern(emoticons, emoji_48, R.drawable.emoji_48);
	    addPattern(emoticons, emoji_49, R.drawable.emoji_49);
	    addPattern(emoticons, emoji_50, R.drawable.emoji_50);
	    addPattern(emoticons, emoji_51, R.drawable.emoji_51);
	    addPattern(emoticons, emoji_52, R.drawable.emoji_52);
	    addPattern(emoticons, emoji_53, R.drawable.emoji_53);
	    addPattern(emoticons, emoji_54, R.drawable.emoji_54);
	    addPattern(emoticons, emoji_55, R.drawable.emoji_55);
	    addPattern(emoticons, emoji_56, R.drawable.emoji_56);
	    addPattern(emoticons, emoji_57, R.drawable.emoji_57);
	    addPattern(emoticons, emoji_58, R.drawable.emoji_58);
	    
	    addPattern(emoticons, emoji_59, R.drawable.emoji_59);//[电话]
	    addPattern(emoticons, emoji_60, R.drawable.emoji_60);//[石头]
	    addPattern(emoticons, emoji_61, R.drawable.emoji_61);//[胜利]
	    addPattern(emoticons, emoji_62, R.drawable.emoji_62);//[禁止]
	    addPattern(emoticons, emoji_63, R.drawable.emoji_63);//[滑雪]
	    addPattern(emoticons, emoji_64, R.drawable.emoji_64);//[高尔夫]
	    addPattern(emoticons, emoji_65, R.drawable.emoji_65);//[网球]
	    addPattern(emoticons, emoji_66, R.drawable.emoji_66);//[棒球]
	    addPattern(emoticons, emoji_67, R.drawable.emoji_67);//[冲浪]
	    addPattern(emoticons, emoji_68, R.drawable.emoji_68);//[足球]
	    addPattern(emoticons, emoji_69, R.drawable.emoji_69);//[小鱼]
	    addPattern(emoticons, emoji_70, R.drawable.emoji_70);//[问号]
	    addPattern(emoticons, emoji_71, R.drawable.emoji_71);//[叹号]
	    addPattern(emoticons, emoji_72, R.drawable.emoji_72);//[顶]
	    addPattern(emoticons, emoji_73, R.drawable.emoji_73);//[写字]
	    addPattern(emoticons, emoji_74, R.drawable.emoji_74);//[衬衫]
	    addPattern(emoticons, emoji_75, R.drawable.emoji_75);//[小花]
	    addPattern(emoticons, emoji_76, R.drawable.emoji_76);//[郁金香]
	    addPattern(emoticons, emoji_77, R.drawable.emoji_77);//[向日葵]
	    addPattern(emoticons, emoji_78, R.drawable.emoji_78);//[鲜花]
	    addPattern(emoticons, emoji_79, R.drawable.emoji_79);//[椰树]
	    addPattern(emoticons, emoji_80, R.drawable.emoji_80);//[仙人掌]
	    addPattern(emoticons, emoji_81, R.drawable.emoji_81);//[气球]
	    addPattern(emoticons, emoji_82, R.drawable.emoji_82);//[炸弹]
	    addPattern(emoticons, emoji_83, R.drawable.emoji_83);//[喝彩]
	    addPattern(emoticons, emoji_84, R.drawable.emoji_84);//[剪子]
	    addPattern(emoticons, emoji_85, R.drawable.emoji_85);//[蝴蝶结]
	    addPattern(emoticons, emoji_86, R.drawable.emoji_86);//[机密]
	    addPattern(emoticons, emoji_87, R.drawable.emoji_87);//[铃声]
	    addPattern(emoticons, emoji_88, R.drawable.emoji_88);//[女帽]
	    addPattern(emoticons, emoji_89, R.drawable.emoji_89);//[裙子]
	    addPattern(emoticons, emoji_90, R.drawable.emoji_90);//[理发店]
	    addPattern(emoticons, emoji_91, R.drawable.emoji_91);//[和服]
	    addPattern(emoticons, emoji_92, R.drawable.emoji_92);//[比基尼]
	    addPattern(emoticons, emoji_93, R.drawable.emoji_93);//[拎包]
	    addPattern(emoticons, emoji_94, R.drawable.emoji_94);//[拍摄]
	    addPattern(emoticons, emoji_95, R.drawable.emoji_95);//[铃铛]
	    addPattern(emoticons, emoji_96, R.drawable.emoji_96);//[音乐]
	    addPattern(emoticons, emoji_97, R.drawable.emoji_97);//[心星]
	    addPattern(emoticons, emoji_98, R.drawable.emoji_98);//[粉心]
	    addPattern(emoticons, emoji_99, R.drawable.emoji_99);//[丘比特]
	    addPattern(emoticons, emoji_100, R.drawable.emoji_100);//[吹气]
	    addPattern(emoticons, emoji_101, R.drawable.emoji_101);//[口水]
	    addPattern(emoticons, emoji_102, R.drawable.emoji_102);//[对]
	    addPattern(emoticons, emoji_103, R.drawable.emoji_103);//[错]
	    addPattern(emoticons, emoji_104, R.drawable.emoji_104);//[绿茶]
	    addPattern(emoticons, emoji_105, R.drawable.emoji_105);//[面包]
	    addPattern(emoticons, emoji_106, R.drawable.emoji_106);//[面条]
	    addPattern(emoticons, emoji_107, R.drawable.emoji_107);//[咖喱饭]
	    addPattern(emoticons, emoji_108, R.drawable.emoji_108);//[饭团]
	    addPattern(emoticons, emoji_109, R.drawable.emoji_109);//[麻辣烫]
	    addPattern(emoticons, emoji_110, R.drawable.emoji_110);//[寿司]
	    addPattern(emoticons, emoji_111, R.drawable.emoji_111);//[苹果]
	    addPattern(emoticons, emoji_112, R.drawable.emoji_112);//[橙子]
	    addPattern(emoticons, emoji_113, R.drawable.emoji_113);//[草莓]
	    addPattern(emoticons, emoji_114, R.drawable.emoji_114);//[西瓜]
	    addPattern(emoticons, emoji_115, R.drawable.emoji_115);//[柿子]
	    addPattern(emoticons, emoji_116, R.drawable.emoji_116);//[眼睛]
	    addPattern(emoticons, emoji_117, R.drawable.emoji_117);//[好的]
	}

	public static void addPattern(Map<Pattern, Integer> map, String smile,
	        int resource) {
	    map.put(Pattern.compile(Pattern.quote(smile)), resource);
	}
	
	public static void addPattern( String smile,
			int resource) {
		emoticons.put(Pattern.compile(Pattern.quote(smile)), resource);
	}

	/**
	 * replace existing spannable with smiles
	 * @param context
	 * @param spannable
	 * @return
	 */
	public static boolean addSmiles(Context context, Spannable spannable) {
	    boolean hasChanges = false;
	    for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(spannable);
	        while (matcher.find()) {
	            boolean set = true;
	            for (ImageSpan span : spannable.getSpans(matcher.start(),
	                    matcher.end(), ImageSpan.class))
	                if (spannable.getSpanStart(span) >= matcher.start()
	                        && spannable.getSpanEnd(span) <= matcher.end())
	                    spannable.removeSpan(span);
	                else {
	                    set = false;
	                    break;
	                }
	            if (set) {
	                hasChanges = true;
	                spannable.setSpan(new ImageSpan(context, entry.getValue()),
	                        matcher.start(), matcher.end(),
	                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
	            }
	        }
	    }
	    return hasChanges;
	}

	public static Spannable getSmiledText(Context context, CharSequence text) {
	    Spannable spannable = spannableFactory.newSpannable(text);
	    addSmiles(context, spannable);
	    return spannable;
	}
	
	public static boolean containsKey(String key){
		boolean b = false;
		for (Entry<Pattern, Integer> entry : emoticons.entrySet()) {
	        Matcher matcher = entry.getKey().matcher(key);
	        if (matcher.find()) {
	        	b = true;
	        	break;
	        }
		}
		
		return b;
	}
	
	
	
}

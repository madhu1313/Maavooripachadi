package com.maavooripachadi.payments.settlement.util;


import java.util.*;


public class Csv {
    public static List<String[]> parse(String content){
        List<String[]> out = new ArrayList<>();
        StringBuilder cell = new StringBuilder();
        List<String> row = new ArrayList<>();
        boolean inQuotes = false;
        for (int i=0;i<content.length();i++){
            char c = content.charAt(i);
            if (inQuotes){
                if (c=='"'){
                    if (i+1 < content.length() && content.charAt(i+1)=='"'){ cell.append('"'); i++; }
                    else { inQuotes = false; }
                } else { cell.append(c); }
            } else {
                if (c=='"') inQuotes = true;
                else if (c==','){ row.add(cell.toString()); cell.setLength(0); }
                else if (c=='\n'){ row.add(cell.toString()); out.add(row.toArray(new String[0])); row = new ArrayList<>(); cell.setLength(0); }
                else if (c=='\r'){ /* ignore */ }
                else { cell.append(c); }
            }
        }
        row.add(cell.toString()); out.add(row.toArray(new String[0]));
        return out;
    }
}
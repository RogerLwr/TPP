package com.tianpingpai.http.util;

import com.tianpingpai.parser.ModelParser;
import com.tianpingpai.parser.ModelResult;

public class ErrorReportParser extends ModelParser<Void> {
    public ErrorReportParser(Class<Void> parseClass) {
        super(parseClass);
    }

    @Override
    public ModelResult<Void> parse(String is) {
        ModelResult<Void> r = null;
        try{
            r = super.parse(is);
        }catch(Exception e){

        }
        return r;
    }
}

package com.digicular.affiliateBuddy.data;

import java.util.HashMap;
import java.util.regex.Pattern;

public class SiteDetector {
    protected HashMap<Pattern, Integer> sitesMap = new HashMap<>();
//    protected static final int AMAZON_IN = 100;
//    protected static final int AMAZON_COM = 101;
//    protected static final int FLIPKART = 200;
//    protected static final int GEARBEST = 300;

    public SiteDetector() {
        sitesMap.put(Pattern.compile("^(https:\\/\\/|http:\\/\\/)?(www[.])?([aA]mazon[.]in)(\\/).{0,}?$"), AppContract.AMAZON_TYPE_CODE);
        sitesMap.put(Pattern.compile("^(https:\\/\\/|http:\\/\\/)?(www[.])?([aA]mazon[.]com)(\\/).{0,}?$"), AppContract.AMAZON_TYPE_CODE);
        sitesMap.put(Pattern.compile("^(https:\\/\\/|http:\\/\\/)?(www[.]|dl[.])?([fF]lipkart[.]com)(\\/).{0,}?$"), AppContract.FLIPKART_TYPE_CODE);
        sitesMap.put(Pattern.compile("^(https:\\/\\/|http:\\/\\/)?(www[.]|m[.])?([gG]earbest[.]com)(\\/).{0,}?$"), AppContract.GEARBEST_TYPE_CODE);
        sitesMap.put(Pattern.compile("^(https:\\/\\/|http:\\/\\/)?([gG]earbest[.])?(app[.]link)(\\/).{0,}?$"), AppContract.GEARBEST_TYPE_CODE);
    }

    /*
     * @param url should be a String
     * @param code should not be 0
     * */
    public int addSite(String urlPatternString, int code) {
        if (urlPatternString != null && code != 0) {
            Pattern urlPattern = Pattern.compile(urlPatternString);
            sitesMap.put(urlPattern, code);
            return 0;
        }
        // Indicates Site addition failed
        return -1;
    }

    public int detectSite(String url) {
        int siteCode = -1;
        for (Pattern urlPattern : sitesMap.keySet()) {
            if (urlPattern.matcher(url).matches()) {
                siteCode = sitesMap.get(urlPattern);
                break;
            }
        }
        return siteCode;
    }


}

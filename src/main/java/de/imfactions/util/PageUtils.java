package de.imfactions.util;

import java.util.ArrayList;

public class PageUtils {

    /**
     * @param list       ArrayList with all entrys
     * @param anzPerPage How many entrys per Page
     * @param page       Which page you want the entrys from
     * @return Returns a ArrayList with the entrys of a page
     */
    public static ArrayList getPage(ArrayList list, int anzPerPage, int page) {
        ArrayList pageList = new ArrayList<>();
        for (int i = page * anzPerPage; i < (page + 1) * anzPerPage && i < list.size(); i++) {
            pageList.add(list.get(i));
        }
        return pageList;
    }

    /**
     * @param list       ArrayList with all entrys
     * @param anzPerPage How many entrys per Page
     * @param page       Which page you want the entrys from
     * @return Returns if the list has entrys for this page
     */
    public static boolean hasPage(ArrayList list, int anzPerPage, int page) {
        if ((double) list.size() / (double) anzPerPage > page && page >= 0) {
            return true;
        }
        return false;
    }

    /**
     * @param list       ArrayList with all entrys
     * @param anzPerPage How many entrys per Page
     * @return Returns how many pages with entrys the ArrayList has
     */
    public static int maxPage(ArrayList list, int anzPerPage) {
        return (int) Math.ceil((double) list.size() / (double) anzPerPage);
    }

}

package searchengine;


import searchengine.model.Site;
import searchengine.model.Status;
import searchengine.services.MorphologyServiceImpl;

public class Test {
    private static String queryRequest = "Настройка поиска рекламы";
    private static Site site = new Site(Status.INDEXING, null,"http://www.google.com", "Google");
    public static void main(String[]args) throws Exception{
        String word = "Настройка";
        MorphologyServiceImpl morphologyService = new MorphologyServiceImpl();
        System.out.println(morphologyService.getNormalFormWord(word));
    }

    public static boolean isValidationLink(String link) {
        String regexRoot = site.getUrl().concat("[[/\\w]+/?]*");
        System.out.println(regexRoot);
        return link.matches(regexRoot);
    }
}
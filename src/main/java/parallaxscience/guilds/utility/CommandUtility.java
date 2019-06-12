package parallaxscience.guilds.utility;

import java.util.ArrayList;
import java.util.List;

public class CommandUtility
{
    /**
     * Finds the matching strings in the list for the last given argument
     * @param args String array of arguments given from the player
     * @param list String list to match the last argument to
     * @return String List of matching Strings
     */
    public static List<String> getLastMatchingStrings(String[] args, List<String> list)
    {
        List<String> matching = new ArrayList<>();
        String string = args[args.length - 1];
        int length = string.length();
        for(String item : list)
        {
            if(item.length() >= length) if(string.toLowerCase().equals(item.substring(0, length).toLowerCase())) matching.add(item);
        }
        return matching;
    }
}

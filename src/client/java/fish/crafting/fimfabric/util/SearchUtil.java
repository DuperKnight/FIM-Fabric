package fish.crafting.fimfabric.util;

import java.util.function.Function;

public class SearchUtil {

    public static <Obj> Obj searchByLowest(Iterable<Obj> iterable, Function<Obj, Double> weight){
        Double lowest = null;
        Obj returnValue = null;

        for (Obj obj : iterable) {
            double w = weight.apply(obj);
            if(lowest == null || w < lowest){
                lowest = w;
                returnValue = obj;
            }
        }

        return returnValue;
    }

    public static <Obj> Obj searchByLowest(Obj[] iterable, Function<Obj, Double> weight){
        Double lowest = null;
        Obj returnValue = null;

        for (Obj obj : iterable) {
            double w = weight.apply(obj);
            if(lowest == null || w < lowest){
                lowest = w;
                returnValue = obj;
            }
        }

        return returnValue;
    }

}

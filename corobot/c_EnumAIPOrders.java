package corobot;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum c_EnumAIPOrders 
{
	STAY_CLOSE, STAY_AROUND, WANDER;
	
	private static final Map<Integer, c_EnumAIPOrders> lookup = new HashMap<Integer, c_EnumAIPOrders>();
    static { for(c_EnumAIPOrders e : EnumSet.allOf(c_EnumAIPOrders.class)) { lookup.put(e.ordinal(), e); } }
    public static c_EnumAIPOrders get(int intValue) { return lookup.get(intValue); }
    
    public c_EnumAIPOrders next() { int pos = this.ordinal() + 1; if (pos >= lookup.size()) pos = 0; return lookup.get(pos); }
    public c_EnumAIPOrders prev() { int pos = this.ordinal() - 1; if (pos < 0) pos = lookup.size() - 1; return lookup.get(pos); }
}

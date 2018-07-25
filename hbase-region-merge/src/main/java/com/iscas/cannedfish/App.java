package com.iscas.cannedfish;

import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HRegionInfo;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.util.Bytes;

public class App 
{
    public static void main( String[] args )
    {
        if(args.length < 3) {
            System.out.println("Usage: cmd <hbase_master> <zookeeper> <table_name>");
            System.out.println("  e.g. cmd bigdata-229:60000 bigdata-227 zt_json");
            System.exit(1);
        }
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.master", args[0]);
        conf.set("hbase.zookeeper.quorum", args[1]);
        
        try {
            HBaseAdmin admin = new HBaseAdmin(conf);
            for(int i = 2; i < args.length; ++i) {
                System.out.println("Start to handle regions in table " + args[i]);
                List<HRegionInfo> regions = admin.getTableRegions(TableName.valueOf(args[i]));
                Collections.sort(regions, new Comparator<HRegionInfo>() {
                    public int compare(HRegionInfo r1, HRegionInfo r2) {
                        return Bytes.compareTo(r1.getStartKey(), r2.getStartKey());
                    }
                });

                HRegionInfo preRegion = null;
                for(HRegionInfo r: regions) {
                    int index = regions.indexOf(r);
                    if(index%2 == 0) {
                        preRegion = r;
                    } else {
                        System.out.println("Start to merge two region");
                        admin.mergeRegions(preRegion.getEncodedNameAsBytes(), r.getEncodedNameAsBytes(), false);
                        System.out.println("Merge two region finished");
                    }
                }
            }
            System.out.println("Merge all region finished");
        } catch (Exception e) {
            e.printStackTrace();
        } 
    }
}

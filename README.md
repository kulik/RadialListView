RadialListView
==============
Radial Adapter View. So you could useit for your own menu. With long list.


Video example
https://youtu.be/I-0ai8VmhPI

Example: 

     <com.kulik.radial.RadialListView
            xmlns:radial="http://schemas.android.com/apk/res-auto"
            android:layout_width="245dp"
            android:layout_height="245dp"
            radial:delay_per_item_anim="1000"
            radial:delemiter="@drawable/polosochka"
            radial:delemiter_width="2dp"
            radial:horisontal_gravity="left"
            />
            
            
radial:delay_per_item_anim -- time of animation
radial:delemiter -- resource that will be used for delemiters
radial:delemiter_width -- delemiter width.
radial:horisontal_gravity -- now it is supports only 2 gravities left or right. Both is bottom. So you could make two radial menu on your choice.
radial:vertical_gravity -- not supported now, it is for future two values bottom (like now) or top           
            
Android ListView like a sector.

Maven / Gradle resource

    compile 'com.github.kulik:radial-list-view:+'



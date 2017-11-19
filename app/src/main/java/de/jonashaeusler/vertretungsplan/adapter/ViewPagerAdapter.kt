package de.jonashaeusler.vertretungsplan.adapter

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter

/**
 * Adapter class to be used with a ViewPager.
 */
class ViewPagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment = mFragmentList[position]

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence = mFragmentTitleList[position]

    /**
     * Add a fragment to the ViewPager.
     *
     * @param fragment The fragment to be added
     * @param title The title to be shown in the tab.
     */
    fun addFragment(fragment: Fragment, title: String = "") {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    /**
     * Retrieve a previously added fragment.
     *
     * @param position The position of the fragment to be retrieved.
     * @return Returns the fragment at index [position].
     */
    fun getFragment(position: Int): Fragment = mFragmentList[position]

    /**
     * @return Returns all added fragments.
     */
    fun getAllFragments(): ArrayList<Fragment> = mFragmentList
}
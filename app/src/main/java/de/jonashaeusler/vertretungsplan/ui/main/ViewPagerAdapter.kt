package de.jonashaeusler.vertretungsplan.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.viewpager.widget.PagerAdapter

/**
 * Adapter class to be used with a ViewPager.
 */
class ViewPagerAdapter(
        fm: FragmentManager
) : FragmentStatePagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    
    private val mFragmentList = ArrayList<Fragment>()
    private val mFragmentTitleList = ArrayList<String>()

    override fun getItem(position: Int): Fragment = mFragmentList[position]

    override fun getCount(): Int = mFragmentList.size

    override fun getPageTitle(position: Int): CharSequence = mFragmentTitleList[position]

    override fun getItemPosition(`object`: Any) = PagerAdapter.POSITION_NONE

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
    fun getFragment(position: Int): Fragment? {
        return if (mFragmentList.size > position) {
            mFragmentList[position]
        } else {
            null
        }
    }

    /**
     * Returns the index of the first occurrence of the
     * specified [fragment] or -1 if there is no such fragment.
     */
    fun indexOf(fragment: Fragment?): Int {
        return mFragmentList.indexOf(fragment)
    }

    /**
     * Remove a fragment previously added.
     *
     * @param position The fragment to remove.
     */
    fun removeFragment(position: Int) {
        if (mFragmentList.size > position) {
            mFragmentList.removeAt(position)
            mFragmentTitleList.removeAt(position)
        }
    }

    /**
     * @return Returns all added fragments.
     */
    fun getAllFragments(): ArrayList<Fragment> = mFragmentList

    /**
     * @return The amount of added fragments.
     */
    fun size(): Int = mFragmentList.size
}

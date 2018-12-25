package com.hitanshudhawan.livedata

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.OnLifecycleEvent
import android.support.annotation.MainThread

class LiveData<T> {

    private var mValue: T? = null
    private var mVersion = 0

    private val mObservers: HashMap<(T?) -> Unit, LiveDataLifecycleObserver> = HashMap()

    /**
     * Sets the value. If there are active observers, the value will be dispatched to them.
     *
     * @param value The new value
     */
    @MainThread
    fun setValue(value: T) {
        mValue = value
        mVersion++

        for (lifecycleObserver in mObservers.values) {
            val owner = lifecycleObserver.owner
            if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                notifyChange(lifecycleObserver)
        }
    }

    /**
     * Returns the current value.
     *
     * @return the current value
     */
    @MainThread
    fun getValue(): T? {
        return mValue
    }

    /**
     * Adds the given observer to the observers list within the lifespan of the given
     * owner. The events are dispatched on the main thread. If LiveData already has data
     * set, it will be delivered to the observer.
     *
     * The observer will only receive events if the owner is in Lifecycle.State#STARTED
     * or Lifecycle.State#RESUMED state (active).
     *
     * If the owner moves to the Lifecycle.State#DESTROYED state, the observer will
     * automatically be removed.
     *
     * When data changes while the owner is not active, it will not receive any updates.
     * If it becomes active again, it will receive the last available data automatically.
     *
     * LiveData keeps a strong reference to the observer and the owner as long as the
     * given LifecycleOwner is not destroyed. When it is destroyed, LiveData removes references to
     * the observer & the owner.
     *
     * If the given owner is already in Lifecycle.State#DESTROYED state, LiveData
     * ignores the call.
     *
     * If the given owner, observer tuple is already in the list, LiveData throws an IllegalArgumentException
     * If the observer is already in the list with another owner, LiveData throws an IllegalArgumentException
     *
     * @param owner    The LifecycleOwner which controls the observer
     * @param observer The observer that will receive the events
     */
    @MainThread
    fun observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
        if (owner.lifecycle.currentState == Lifecycle.State.DESTROYED)
            return
        if (mObservers[observer] != null)
            throw IllegalArgumentException("This observer is already attached to an owner")

        val lifecycleObserver = LiveDataLifecycleObserver(owner, observer)
        mObservers[observer] = lifecycleObserver
        owner.lifecycle.addObserver(lifecycleObserver)
    }

    /**
     * Removes the given observer from the observers list.
     *
     * @param observer The Observer to receive events.
     */
    @MainThread
    fun removeObserver(observer: (T?) -> Unit) {
        val lifecycleObserver = mObservers.remove(observer)
        lifecycleObserver?.owner?.lifecycle?.removeObserver(lifecycleObserver)
    }

    private fun notifyChange(lifecycleObserver: LiveDataLifecycleObserver) {
        if (mVersion > lifecycleObserver.version) {
            lifecycleObserver.version = mVersion
            lifecycleObserver.observer.invoke(mValue)
        }
    }

    private inner class LiveDataLifecycleObserver(val owner: LifecycleOwner, val observer: (T?) -> Unit) : LifecycleObserver {

        var version = 0

        @OnLifecycleEvent(Lifecycle.Event.ON_START)
        private fun onStarted() {
            notifyChange(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        private fun onResumed() {
            notifyChange(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        private fun onDestroyed() {
            removeObserver(observer)
        }

    }

}
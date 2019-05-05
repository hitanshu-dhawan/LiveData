Full article here: https://medium.com/androidiots/how-i-made-my-own-livedata-1faf4a45520

# LiveData
> LiveData is an observable data holder class. Unlike a regular observable, LiveData is lifecycle-aware, meaning it respects the lifecycle of other app components, such as activities, fragments, or services. This awareness ensures LiveData only updates app component observers that are in an active lifecycle state.

<br>

```
class LiveData<T> {

    private var mValue: T? = null
    private val mObservers: HashMap<(T?) -> Unit, LiveDataLifecycleObserver> = HashMap()

    fun setValue(value: T) {
        mValue = value

        for (lifecycleObserver in mObservers.values) {
            val owner = lifecycleObserver.owner
            if (owner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED))
                notifyChange(lifecycleObserver)
        }
    }

    fun getValue(): T? {
        return mValue
    }

    fun observe(owner: LifecycleOwner, observer: (T?) -> Unit) {
        val lifecycleObserver = LiveDataLifecycleObserver(owner, observer)
        mObservers[observer] = lifecycleObserver
        owner.lifecycle.addObserver(lifecycleObserver)
    }

    fun removeObserver(observer: (T?) -> Unit) {
        val lifecycleObserver = mObservers.remove(observer)
        lifecycleObserver?.owner?.lifecycle?.removeObserver(lifecycleObserver)
    }

    private fun notifyChange(lifecycleObserver: LiveDataLifecycleObserver) {
        lifecycleObserver.observer.invoke(mValue)
    }

    private inner class LiveDataLifecycleObserver(val owner: LifecycleOwner, val observer: (T?) -> Unit) : LifecycleObserver {

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
```

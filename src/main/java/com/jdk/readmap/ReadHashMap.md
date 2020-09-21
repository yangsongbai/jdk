# 属性 
HashMap作为一种数据结构，元素在put的过程中需要进行hash运算，目的是计算出该元素存放在hashMap中的具体位置。
hash运算的过程其实就是对目标元素的Key进行hashcode，再对Map的容量进行取模，
而JDK 的工程师为了提升取模的效率，使用位运算代替了取模运算，这就要求Map的容量一定得是2的幂。
而作为默认容量，太大和太小都不合适，所以16就作为一个比较合适的经验值被采用了。
static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16  
HashMap内部由Entry[]数组构成，Java的数组下标是由Int表示的。所以对于HashMap来说其最大的容量应该是不超过int最大值的一个2的指数幂，
而最接近int最大值的2个指数幂用位运算符表示就是 1 << 30
static final int MAXIMUM_CAPACITY = 1 << 30;
负载因子太小了浪费空间并且会发生更多次数的resize，太大了哈希冲突增加会导致性能不好，所以0.75只是一个折中的选择，和泊松分布没有什么关系
static final float DEFAULT_LOAD_FACTOR = 0.75f;
根据泊松分布概率质量函数,一个哈希桶达到 9 个元素的概率小于一千万分之一. 选定阈值为 8 猜测是基于泊松分布.
由代码我们得知，树化和树退化方法的判断都是闭区间，如果都是 8,则可能陷入(树化<=>树退化)的死循环中. 
若是 7,则当极端情况下(频繁插入和删除的都是同一个哈希桶)对一个链表长度为 8 的的哈希桶进行频繁的删除和插入，
同样也会导致频繁的树化<=>非树化.
由此,选定 6 的原因一部分是需要低于 8，但过于接近也会导致频繁的结构变化
static final int TREEIFY_THRESHOLD = 8;
由源码可知,TreeNode 光是属性数就多于 Node.故,
不选定低于 6 的退化阈值一方面是因为红黑树不一定在低元素时效率更好
（事实上由 MIN_TREEIFY_CAPACITY=64 参数，只有容量大于 64 时才会开启树化），
另一方面则是红黑树相比链表占用了更多的引用
static final int UNTREEIFY_THRESHOLD = 6;
 // 为了避免进行扩容、树形化选择的冲突，这个值不能小于 4 * TREEIFY_THRESHOLD
static final int MIN_TREEIFY_CAPACITY = 64;
# 方法  

## 构造方法  

```
 public HashMap(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal initial capacity: " +
                                               initialCapacity);
        if (initialCapacity > MAXIMUM_CAPACITY)
            initialCapacity = MAXIMUM_CAPACITY;
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal load factor: " +
                                               loadFactor);
        this.loadFactor = loadFactor;
        this.threshold = tableSizeFor(initialCapacity);
    }
```

## put 
 https://blog.csdn.net/ZLB_CSDN/article/details/108649462
```
 public V put(K key, V value) {
        return putVal(hash(key), key, value, false, true);
    }
/**
这段代码是为了对key的hashCode进行扰动计算，防止不同hashCode的高位不同但低位相同导致的hash冲突。
简单点说，就是为了把高位的特征和低位的特征组合起来，降低哈希冲突的概率
也就是说，尽量做到任何一位的变化都能对最终得到的结果产生影响。

由于绝大多数情况下length一般都小于2^16即小于65536。
所以return h & (length-1);结果始终是h的低16位与（length-1）进行&运算
*/
   static final int hash(Object key) {
        int h;
     //因为&和|都会使得结果偏向0或者1 ,并不是均匀的概念,所以用^。
        return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
    }
```

```
 final V putVal(int hash, K key, V value, boolean onlyIfAbsent,
                   boolean evict) {
        Node<K,V>[] tab; Node<K,V> p; int n, i;
        if ((tab = table) == null || (n = tab.length) == 0)
            n = (tab = resize()).length;
        if ((p = tab[i = (n - 1) & hash]) == null)
            tab[i] = newNode(hash, key, value, null);
        else {
            Node<K,V> e; K k;
            if (p.hash == hash &&
                ((k = p.key) == key || (key != null && key.equals(k))))
                e = p;
            else if (p instanceof TreeNode)
                e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
            else {
                for (int binCount = 0; ; ++binCount) {
                    if ((e = p.next) == null) {
                        p.next = newNode(hash, key, value, null);
                        if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st
                            treeifyBin(tab, hash);
                        break;
                    }
                    if (e.hash == hash &&
                        ((k = e.key) == key || (key != null && key.equals(k))))
                        break;
                    p = e;
                }
            }
            if (e != null) { // existing mapping for key
                V oldValue = e.value;
                if (!onlyIfAbsent || oldValue == null)
                    e.value = value;
                afterNodeAccess(e);
                return oldValue;
            }
        }
        ++modCount;
        if (++size > threshold)
            resize();
        afterNodeInsertion(evict);
        return null;
    }
```
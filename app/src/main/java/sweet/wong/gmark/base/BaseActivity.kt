package sweet.wong.gmark.base

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import java.lang.reflect.ParameterizedType

abstract class BaseActivity<T : ViewBinding> : AppCompatActivity() {

    protected lateinit var binding: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewBinding()
        setContentView(binding.root)
    }

    @Suppress("UNCHECKED_CAST")
    private fun initViewBinding() {
        val viewBindingClass = (this::class.java.genericSuperclass as ParameterizedType)
            .actualTypeArguments[0] as Class<*>
        val inflateMethod = viewBindingClass.getDeclaredMethod(
            "inflate",
            LayoutInflater::class.java
        )
        binding = inflateMethod.invoke(null, layoutInflater) as T
    }


}
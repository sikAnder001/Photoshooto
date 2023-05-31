package `in`.bizanalyst.utils.extensions

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract

/**
 * @param context
 * @return phone/mobile number
 */
fun Intent.getNumber(context: Context): String? {
    val projection = arrayOf(
        ContactsContract.CommonDataKinds.Phone.NUMBER,
    )
    context.contentResolver.query(
        data!!, projection, null, null, null
    ).use { cursor ->
        cursor?.let {
            if (it.moveToNext()) {
                val index =
                    it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (index >= 0) {
                    return it.getString(index)
                        .replace("\\s+", "")
                }
            }
        }
    }
    return null
}
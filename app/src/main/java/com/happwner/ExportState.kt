package com.happwner

import androidx.lifecycle.ViewModel

/** Keep large pending exports out of saved-instance Bundles and survive Activity recreation. */
class ExportState : ViewModel() {
    var text: String? = null
}

import {isCartEmpty, setSelectedVariant, variants, animate, sendRequest, selectedVariant} from "./app.js";

export function initializeSizes() {
    if (isCartEmpty == null || variants == null) {
        return;
    }
    let div = document.getElementById("sizes");

    if (div == null) {
        return;
    }

    let active = "active-size"
    setSelectedVariant(variants[0])

    document.getElementById("stock").textContent = variants[0].stock


    for (let i = 0; i < variants.length; i++) {
        div.innerHTML += `<button class="badge rounded-pill px-3 py-2 variant me-2 ${active} size-btn" 
                            data-stock="${variants[i].stock}" data-size="${variants[i].size}"
                      style="background: rgba(47,93,98,.10); color: var(--primary); border: 1px solid rgba(47,93,98,.20);"
                      >
                        ${variants[i].size}
                       </button>`
        active = ""
    }
}

export function changeSize(object) {
    let variantsElem = document.getElementsByClassName("size-btn")
    for (let i = 0; i < variantsElem.length; i++) {
        if (object === variantsElem[i]) {
            variantsElem[i].classList.add("active-size")
            let stock = variantsElem[i].dataset.stock
            let size = variantsElem[i].dataset.size
            setSelectedVariant(variants.find(v => v.size === size && v.stock === parseInt(stock)))
            continue
        }
        variantsElem[i].classList.remove("active-size")
    }

    document.getElementById("stock").textContent = object.dataset.stock
}


export function addToCart() {
    animate(sendRequest("quantity-details"))
}

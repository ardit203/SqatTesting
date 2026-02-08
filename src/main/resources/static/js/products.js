import {setSelectedVariant, variants, sendRequest, animate} from "./app.js";


export function addToCartFromProducts(productId) {
    addProductToCartOverlay(variants.filter(v => v.productId == productId))
}

function addProductToCartOverlay(variants) {
    let divs = ""
    for (let variant of variants) {
        divs += `
  <div class="vo-item">
    <div class="vo-item-left">
      <div class="vo-item-size">${variant.size}</div>
      <div class="vo-item-meta">Stock: <span class="vo-stock">${variant.stock}</span></div>
    </div>

    <div class="vo-item-right">
      <span class="vo-chip ${variant.stock > 10 ? "ok" : (variant.stock > 0 ? "low" : "out")}">
        ${variant.stock > 10 ? "In Stock" : (variant.stock > 0 ? "Low" : "Out")}
      </span>
    </div>
    <button type="button" class="btn btn-primary btn-sm vo-btn check-before-request-btn"
            data-size="${variant.size}" data-stock="${variant.stock}">
                Add to cart
    </button>
  </div>
`;

    }

    let overlayHtml = `
  <div id="variantOverlay" class="vo-backdrop">
    <div class="vo-modal">
      <div class="vo-header">
        <div>
          <div class="vo-title">Choose a Variant</div>
          <div class="vo-subtitle">Select size, check stock, and set quantity</div>
        </div>
        <button type="button" class="vo-close close-products-overlay" aria-label="Close">&times;</button>
      </div>

      <div class="vo-body">
        <div class="vo-list">
          ${divs}
        </div>
      </div>

      <div class="vo-footer">
        <div class="vo-qty">
          <label class="vo-label" for="quantity-product">Quantity</label>
          <input type="number" class="form-control form-control-sm vo-qty-input"
                 id="quantity-product" value="1" min="1">
        </div>
      </div>
    </div>
  </div>
`;
    document.body.insertAdjacentHTML("beforeend", overlayHtml);

}

export function closeOverlay() {
    document.getElementById("variantOverlay").remove()
}

export function checkBeforeRequest(object) {
    let stock = parseInt(object.dataset.stock)
    let size = object.dataset.size
    setSelectedVariant(variants.find(v => v.stock == stock && v.size == size))
    let isOk = sendRequest("quantity-product")
    closeOverlay()
    animate(isOk)
}
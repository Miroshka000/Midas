<div align="center">

# 💰 Midas For Allay

**Configurable shop, buyer and auto-buyer system for Allay servers**

![License](https://img.shields.io/badge/License-MIT-blue.svg)
![Version](https://img.shields.io/badge/Version-0.1.0-green.svg)
![Platform](https://img.shields.io/badge/Platform-Allay-orange.svg)

<br>

[![Русский](https://img.shields.io/badge/Language-Русский-red?style=for-the-badge&logo=google-translate&logoColor=white)](README_RU.md)

</div>

---

**Midas** is a configurable economy plugin for **Allay** servers. It provides a form-based shop, a form-based buyer, rotating special offers, and personal auto-buyer terminals bound to chests or barrels.

### ✨ Features
- **Form Shop**: Categories and products are configured in `shop.yml`.
- **Flexible Rewards**: Products can give items, run commands, or apply effects.
- **Buyer**: Players can sell configured items through `/buyer`.
- **Amount Scroll**: Products can scale price by amount or use a fixed amount only.
- **Special Offers**: Rotating discounted shop products and boosted buyer products.
- **Fixed Special Products**: Extra special products with prices already set in config.
- **Auto-buyer**: Bind a chest or barrel and sell supported items through a personal container form.
- **Localization**: Text resources are stored in `assets/lang/en_US.json` and `assets/lang/ru_RU.json`.

### 🎮 Commands
| Command | Permission | Description |
|---------|------------|-------------|
| `/shop` | `midas.command.shop` | Open the shop form. |
| `/buyer` | `midas.command.buyer` | Open the buyer form. |
| `/setautobuyer` | `midas.command.autobuyer` | Bind an auto-buyer chest or barrel. |
| `/delautobuyer` | `midas.command.autobuyer.delete` | Unbind an auto-buyer chest or barrel. |

### ⚙️ Configuration
Midas creates two main YAML files in the plugin data folder:

| File | Description |
|------|-------------|
| `shop.yml` | Shop title, categories, per-product currencies, products and shop special offers. |
| `buyer.yml` | Buyer title, sell categories, per-product currencies, buyer special offers and auto-buyer terminals. |

### 📝 Product Options
| Option | Description |
|--------|-------------|
| `id` | Unique product id inside the category. |
| `name` | Display name in forms. |
| `description` | Product description in forms. |
| `minAmount` | Minimum purchase or sell amount. |
| `price` | Price for `minAmount`. |
| `amountScrollEnabled` | Enables amount selection when `true`; fixed amount when `false`. |
| `maxAmount` | Maximum selectable amount when scrolling is enabled. |

Shop products support `ITEM`, `COMMAND`, and `EFFECT` rewards. Buyer products use an item filter with material, data, display name, lore and enchantments.

### 🔥 Special Offers
Shop special offers refresh every configured interval and include:
- random regular products with a discount from `discountMinPercent` to `discountMaxPercent`;
- configured special products with fixed prices.

Buyer special offers refresh on the same principle and include:
- random regular products with a price multiplier from `multiplierMin` to `multiplierMax`;
- configured special products with fixed prices.

### 📦 Installation
1. Build or download the plugin jar.
2. Place it in the server `plugins` folder.
3. Install required economy dependencies.
4. Restart the server.
5. Edit `shop.yml` and `buyer.yml` if needed.

### 🛠️ Build
```bash
./gradlew build
```

The compiled jar is placed in `build/libs`.

### 🔗 Dependencies
- **Required**: Allay Server `0.27.0` or newer.
- **Required**: EconomyAPI `0.2.2` or newer.
- **Required**: Aconomy `0.2.0` or newer.

---

<div align="center">
    <br>
    <p>Created by <b>Miroshka</b> for <b>Allay</b> with ❤️</p>
</div>

# cordova-plugin-paywithmybank (Trustly)
#### Pala Interactive - Internal documentation
#### author: Dan Shields
#### created: 2023-03-21

# quick overview:

This Cordova plugin is for integration of the Trustly (Pay With My Bank) product with our B2B and Stardust customers.

### status:

Both the Android and iOS implementations are passing local tests.

The web implementation has passed, videos demonstrating iOS and Android ExpiredSplitToken test have been submitted.

### installation in ionic 4:

```bash
npm install --save https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### installation in ionic 2:

```bash
cordova plugin add https://github.com/palainteractive/cordova-plugin-paywithmybank
```

### typescript interface (selectBankWidget):

`data` has the same structure as defined by the web implementation.

```typescript
window.cordova.plugins.PayWithMyBank.selectBankWidget( data, () => {
  // success callback
}, (err) => {
  // error callback
});
```


### typescript interface (establish):

In some cases (e.g. ExpiredSplitToken, Truncated or missing Split Token) the user should be asked to authenticate once more, the `establiosh()` call handles this.

`data` has the same structure as defined by the web implementation.

```typescript
window.cordova.plugins.PayWithMyBank.establish( data, () => {
  // success callback
}, (err) => {
  // error callback
});
```



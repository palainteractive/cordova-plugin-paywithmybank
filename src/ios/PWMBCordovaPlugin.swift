import Foundation
import PayWithMyBank


/**
 * #### Pala Interactive - Internal documentation
 * #### Cordova Plugin - Trustly PayWithMyBank version 2.3.0
 * #### author: Dan Shields
  * #### created: 2023-02-17
 */

protocol PayWithMyBankViewProtocol {
    func onReturnWithTransactionId( transactionId: Any);
    func onCancelWithTransactionId( transactionId: Any?);
}

class PayWithMyBankViewController: UIViewController {
    
    var establishData:Dictionary<AnyHashable,Any>?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        let payWithMyBankPanel = PayWithMyBankView()
        
        let _ = payWithMyBankPanel.selectBankWidget( self.establishData) { (view,data) in
            if let data = data {
                print(  "PWMB: selectBankWidget: data: \(data)")
                self.establishData = data
            }
        }

    }
}

@objc(PWMBCordovaPlugin)
public class PWMBCordovaPlugin: CDVPlugin,PayWithMyBankViewProtocol {
    private var establishData:Dictionary<AnyHashable,Any>?
    var trustly: PayWithMyBankView!
    private var callInProgress: CDVInvokedUrlCommand? = nil

    @objc func selectBankWidget(_ call: CDVInvokedUrlCommand) {
        self.callInProgress = call
        self.establishData = [:]
        let keys = call.options.keys
        for key in keys {
            let val = call.getString( key as! String)
            if( nil != val) {
                self.establishData![key] = val
//                print( "PWMB: selectBankWidget: \(key) == \(String(describing: val))")
            }
        }
                
        DispatchQueue.main.async {
            let merchantViewController = MerchantViewController();
            merchantViewController.delegate = self
            merchantViewController.establishData = self.establishData!;
            guard let bridge = self.bridge else {return}
            bridge.viewController!.present( merchantViewController, animated: true, completion: nil)
        }
    }
    func onReturnWithTransactionId(transactionId: Any) {
        guard let bridge = self.bridge else {return}
        bridge.viewController?.dismiss( animated: true)
        self.callInProgress?.resolve( ["transactionId": transactionId])
    }
    
    func onCancelWithTransactionId(transactionId: Any?) {
        guard let bridge = self.bridge else {return}
        bridge.viewController?.dismiss( animated: true)
        self.callInProgress?.resolve( ["error": "cancelled"])
    }
    
}

import Cordova
import Cordova
import Foundation
import PayWithMyBank


/**
 * #### Pala Interactive - Internal documentation
 * #### Cordova Plugin - Trustly PayWithMyBank v2.3.0
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

@objc(PayWithMyBankCordova)
public class PayWithMyBankCordova: CDVPlugin {
    private var establishData:Dictionary<AnyHashable,Any>?
    var trustly: PayWithMyBankView!
    private var callInProgress: CDVInvokedUrlCommand? = nil

    @objc(selectBankWidget:)
    func selectBankWidget( command: CDVInvokedUrlCommand) {
        self.callInProgress = command
        self.establishData = [:]
        
        let params:AnyObject = command.arguments[0] as AnyObject;
        
        let keys = params.allKeys
        for key in keys! {
            let val = command.value( forKey: key as! String) as? String
            if( nil != val) {
                self.establishData![key as? String] = val!
                print( "PWMB: selectBankWidget: \(key) == \(String(describing: val))")
            }
        }
                
        DispatchQueue.main.async {
            let merchantViewController = MerchantViewController();
            merchantViewController.delegate = PayWithMyBankDelegate(
                viewController: self.viewController!,
                commandDelegate: self.commandDelegate!,
                callInProgress: self.callInProgress!)
            merchantViewController.establishData = self.establishData!;
            self.viewController!.present( merchantViewController, animated: true, completion: nil)
        }
    }
    public class PayWithMyBankDelegate: PayWithMyBankViewProtocol {
        var viewController: UIViewController
        var commandDelegate:CDVCommandDelegate
        var callInProgress:CDVInvokedUrlCommand
        init( viewController: UIViewController, commandDelegate:CDVCommandDelegate, callInProgress:CDVInvokedUrlCommand) {
            self.viewController = viewController
            self.commandDelegate = commandDelegate
            self.callInProgress = callInProgress
        }
        func onReturnWithTransactionId(transactionId: Any) {
            self.viewController.dismiss( animated: true)
            self.commandDelegate.send( CDVPluginResult(
                status: CDVCommandStatus_OK,
                messageAs: "{\"transactionId\": \(transactionId)}"
            ), callbackId: self.callInProgress.callbackId);
        }
        
        func onCancelWithTransactionId(transactionId: Any?) {
            self.viewController.dismiss( animated: true)
            self.commandDelegate.send( CDVPluginResult(
                status: CDVCommandStatus_ERROR,
                messageAs: "{\"error\": \"cancelled\"}"
            ), callbackId:self.callInProgress.callbackId);
        }
    }
    
}



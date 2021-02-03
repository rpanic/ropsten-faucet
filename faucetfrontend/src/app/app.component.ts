import { Component } from '@angular/core';
import { Observable, throwError } from 'rxjs';
import { ApiService } from './api.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent {

  constructor(private api: ApiService){
    this.getBalance().subscribe(x => {
      this.balance = x
    })
    this.updateTxs()
  }
  balance: string
  address: string
  success = true
  message = ""

  txs: Transaction[] = []

  updateTxs(){
    this.api.getTx().subscribe(txs => {
      this.txs = txs;
    })
  }

  getBalance() : Observable<string>{
    return this.api.getBalance()
  }

  timeout = null

  show(){
    if(this.timeout != null){
      clearTimeout(this.timeout)
    }
    eval("$('#message').collapse('show')")
    this.timeout = setTimeout(this.hide, 10000)
  }

  hide(){
    eval("$('#message').collapse('hide')")
    this.timeout = null
  }

  send(){

    this.api.requestEther(this.address)
    .subscribe(x => {
      this.success = x.status === "OK"
      if(this.success){

        this.message = "<a class='text-white' href='https://ropsten.etherscan.io/tx/" + x.message + "' target='_blank'>Sent Transaction <i class='fas fa-external-link-alt'></i></a>"
        this.updateTxs();

      }else{
        this.message = x.message
      }
      this.show()
    },
    error => {
      this.success = error.error.status === "OK"
      this.message = error.error.message
      this.show()
    })

  }

}

export class Transaction{
  constructor(public id: string, public address: string, public date: number){}
}
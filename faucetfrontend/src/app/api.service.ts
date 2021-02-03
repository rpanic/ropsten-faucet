import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Transaction } from './app.component';

@Injectable({
  providedIn: 'root'
})
export class ApiService {

  url = "http://localhost:8080"

  constructor(private http: HttpClient) { }

  getBalance() : Observable<string>{
    return this.http.get(this.url + "/balanceLeft").pipe(map(x => x.toString()))
  }

  requestEther(address: string) : Observable<any>{
    return this.http.get(this.url + "/requestEth?address=" + address)
  }

  getTx() : Observable<Transaction[]>{
    return this.http.get<Transaction[]>(this.url + "/txs")
  }

}

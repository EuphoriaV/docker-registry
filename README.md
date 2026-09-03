# docker-registry

Свой Docker registry на Spring Boot, реализует OCI Distribution Spec. Поддерживает push/pull, в том числе multi-arch образы.

## Пример использования:

```bash
docker tag alpine:latest localhost:5000/alpine:latest
docker push localhost:5000/alpine:latest
docker pull localhost:5000/alpine:latest
```